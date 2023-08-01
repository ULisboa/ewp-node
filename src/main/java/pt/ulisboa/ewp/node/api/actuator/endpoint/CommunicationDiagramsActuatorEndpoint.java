package pt.ulisboa.ewp.node.api.actuator.endpoint;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.host.HttpCommunicationFromHostLog;
import pt.ulisboa.ewp.node.domain.repository.communication.log.CommunicationLogRepository;

@Component
@RestControllerEndpoint(id = "communications")
public class CommunicationDiagramsActuatorEndpoint {

  private static final Logger LOG =
      LoggerFactory.getLogger(CommunicationDiagramsActuatorEndpoint.class);

  private static final String EXECUTABLE = "mmdc";
  private static final String PUPPETEER_CONFIG_FILE = "/opt/puppeteer-config.json";

  private static final String DIAGRAM_OUTPUT_FORMAT = "svg";
  private static final int MAXIMUM_MESSAGE_LINE_LENGTH = 120;

  private final CommunicationLogRepository repository;

  public CommunicationDiagramsActuatorEndpoint(CommunicationLogRepository repository) {
    this.repository = repository;
  }

  // Final URI: /actuator/communications/diagrams/{id}
  @GetMapping("/diagrams/{id}")
  @Transactional
  public byte[] getDiagramForCommunication(@PathVariable("id") long id)
      throws IOException, InterruptedException {
    Optional<CommunicationLog> communicationLogOptional = repository.findById(id);
    if (communicationLogOptional.isEmpty()) {
      return null;
    }
    CommunicationLog communicationLog = communicationLogOptional.get();
    String sequenceDiagramString = createSequenceDiagramInMermaidFormat(communicationLog);
    return generateDiagram(sequenceDiagramString);
  }

  private byte[] generateDiagram(CharSequence sequenceDiagramString) {
    File inputTempFile = null;
    File outputTempFile = null;
    try {
      inputTempFile = File.createTempFile("ewp-node-communication-diagram-", ".in");
      LOG.debug(
          "Preparing to write communication diagram to temporary file: {}",
          inputTempFile.getAbsolutePath());
      FileUtils.write(inputTempFile, sequenceDiagramString, "UTF-8");

      outputTempFile =
          File.createTempFile("ewp-node-communication-diagram-", "." + DIAGRAM_OUTPUT_FORMAT);

      Map<String, String> arguments = new HashMap<>();
      arguments.put("-i", inputTempFile.getAbsolutePath());
      arguments.put("-o", outputTempFile.getAbsolutePath());
      arguments.put("-p", PUPPETEER_CONFIG_FILE);
      Process process =
          Runtime.getRuntime()
              .exec(
                  EXECUTABLE
                      + " "
                      + arguments.entrySet().stream()
                          .map(e -> e.getKey() + " " + e.getValue())
                          .collect(Collectors.joining(" ")));
      process.waitFor();

      if (process.exitValue() != 0) {
        String error = new String(process.getErrorStream().readAllBytes());
        throw new IllegalStateException("Diagram generation failed: \"" + error + "\"");
      }

      String resultString = FileUtils.readFileToString(outputTempFile, "UTF-8");
      // NOTE: on the resulting diagram file the blank character #8203; is converted into ​, so
      // remove those characters
      resultString = resultString.replace("​", "");

      return resultString.getBytes(StandardCharsets.UTF_8);

    } catch (IOException | InterruptedException | IllegalStateException e) {
      LOG.error("Failed to generate diagram", e);
      throw new IllegalStateException(e);
    } finally {
      if (inputTempFile != null) {
        if (!inputTempFile.delete()) {
          LOG.warn("Failed to delete input file: " + inputTempFile.getAbsolutePath());
        }
      }
      if (outputTempFile != null) {
        if (!outputTempFile.delete()) {
          LOG.warn("Failed to delete output file: " + outputTempFile.getAbsolutePath());
        }
      }
    }
  }

  private String createSequenceDiagramInMermaidFormat(CommunicationLog communicationLog)
      throws IOException {
    StringBuilder diagramBuilder = new StringBuilder();
    diagramBuilder.append("sequenceDiagram").append(System.lineSeparator());
    Map<String, String> participantToAliasMap = new HashMap<>();
    fillBuilderWithSequenceDiagramInMermaidFormat(
        diagramBuilder, communicationLog, participantToAliasMap);
    return diagramBuilder.toString();
  }

  private void fillBuilderWithSequenceDiagramInMermaidFormat(
      StringBuilder diagramBuilder,
      CommunicationLog communicationLog,
      Map<String, String> participantToAliasMap)
      throws IOException {

    if (communicationLog instanceof HttpCommunicationLog) {
      HttpCommunicationLog httpCommunicationLog = (HttpCommunicationLog) communicationLog;
      HttpRequestLog request = httpCommunicationLog.getRequest();
      String requesterName = getRequesterName(httpCommunicationLog);
      registerParticipantIfMissing(diagramBuilder, participantToAliasMap, requesterName);
      String requesterAlias = participantToAliasMap.get(requesterName);
      String receiverName = request.getUrl();
      registerParticipantIfMissing(diagramBuilder, participantToAliasMap, receiverName);
      String receiverAlias = participantToAliasMap.get(receiverName);
      registerCommunication(
          diagramBuilder,
          requesterAlias,
          receiverAlias,
          "->>",
          request.toRawString(MAXIMUM_MESSAGE_LINE_LENGTH));

      for (CommunicationLog childCommunicationLog :
          communicationLog.getSortedChildrenCommunications()) {
        fillBuilderWithSequenceDiagramInMermaidFormat(
            diagramBuilder, childCommunicationLog, participantToAliasMap);
      }

      String observations = communicationLog.getObservations();
      if (!StringUtils.isBlank(observations)) {
        registerNote(
            diagramBuilder,
            receiverAlias,
            pt.ulisboa.ewp.node.utils.StringUtils.breakTextWithLineLengthLimit(
                observations, System.lineSeparator(), MAXIMUM_MESSAGE_LINE_LENGTH));
      }

      HttpResponseLog response = httpCommunicationLog.getResponse();
      registerCommunication(
          diagramBuilder,
          receiverAlias,
          requesterAlias,
          "-->>",
          response.toRawString(MAXIMUM_MESSAGE_LINE_LENGTH));

    } else {
      throw new IllegalArgumentException(
          "Unsupported communication log type: " + communicationLog.getClass().getSimpleName());
    }
  }

  private String getRequesterName(HttpCommunicationLog communicationLog) {
    if (communicationLog.getParentCommunication() != null) {
      if (communicationLog.getParentCommunication() instanceof HttpCommunicationLog) {
        HttpCommunicationLog parentHttpCommunicationLog =
            (HttpCommunicationLog) communicationLog.getParentCommunication();
        return parentHttpCommunicationLog.getRequest().getUrl();
      } else {
        throw new IllegalArgumentException(
            "Unsupported communication log type: " + communicationLog.getClass().getSimpleName());
      }
    }

    if (communicationLog instanceof HttpCommunicationFromHostLog) {
      HttpCommunicationFromHostLog communicationFromHostLog =
          (HttpCommunicationFromHostLog) communicationLog;
      if (communicationFromHostLog.getHostForwardEwpApiClient() != null) {
        return communicationFromHostLog.getHostForwardEwpApiClient().getId();
      }
    }

    return "Requester";
  }

  private void registerParticipantIfMissing(
      StringBuilder diagramBuilder, Map<String, String> participantToAliasMap, String participant) {
    if (!participantToAliasMap.containsKey(participant)) {
      String nextAlias = "p" + (participantToAliasMap.size() + 1);
      participantToAliasMap.put(participant, nextAlias);
      diagramBuilder
          .append("\tparticipant ")
          .append(nextAlias)
          .append(" as ")
          .append(participant)
          .append(System.lineSeparator());
    }
  }

  private void registerCommunication(
      StringBuilder diagramBuilder,
      String requesterAlias,
      String receiverAlias,
      String arrowType,
      String message) {
    // NOTE: the generation of the diagram fails if a line ends in whitespace (e.g. <br>), therefore
    // a blank character (#8203;) is used.
    String sanitizedMessage =
        message.replace(";", "#59;").replace(System.lineSeparator(), "<br>#8203;");

    diagramBuilder
        .append("\t")
        .append(requesterAlias)
        .append(" ")
        .append(arrowType)
        .append(" ")
        .append(receiverAlias)
        .append(": ")
        .append(sanitizedMessage)
        .append(System.lineSeparator());
  }

  private void registerNote(
      StringBuilder diagramBuilder, String placeAtRightOfAlias, String message) {
    // NOTE: the generation of the diagram fails if a line ends in whitespace (e.g. <br>), therefore
    // a blank character (#8203;) is used.
    String sanitizedMessage =
        message.replace(";", "#59;").replace(System.lineSeparator(), "<br>#8203;");

    diagramBuilder
        .append("\t")
        .append("Note right of ")
        .append(placeAtRightOfAlias)
        .append(": ")
        .append(sanitizedMessage)
        .append(System.lineSeparator());
  }
}
