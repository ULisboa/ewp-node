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
import pt.ulisboa.ewp.node.domain.entity.communication.log.function.call.FunctionCallArgumentLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.host.plugin.HostPluginFunctionCallCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationFromEwpNodeLog;
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

    String requesterName = getRequesterName(communicationLog);
    registerParticipantIfMissing(diagramBuilder, participantToAliasMap, requesterName);
    String requesterAlias = participantToAliasMap.get(requesterName);
    String receiverName = getEntityName(communicationLog);
    registerParticipantIfMissing(diagramBuilder, participantToAliasMap, receiverName);
    String receiverAlias = participantToAliasMap.get(receiverName);

    registerCommunication(
            diagramBuilder,
            requesterAlias,
            receiverAlias,
            "->>",
            getRequestAsString(communicationLog));

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

    String exceptionStacktrace = communicationLog.getExceptionStacktrace();
    if (!StringUtils.isBlank(exceptionStacktrace)) {
      registerExceptionStacktrace(
              diagramBuilder,
              receiverAlias,
              pt.ulisboa.ewp.node.utils.StringUtils.breakTextWithLineLengthLimit(
                      exceptionStacktrace, System.lineSeparator(), MAXIMUM_MESSAGE_LINE_LENGTH));
    }

    registerCommunication(
            diagramBuilder,
            receiverAlias,
            requesterAlias,
            "-->>",
            getResponseAsString(communicationLog));
  }

  private String getRequestAsString(CommunicationLog communicationLog) {
    if (communicationLog instanceof HostPluginFunctionCallCommunicationLog) {
      return getRequestAsString((HostPluginFunctionCallCommunicationLog) communicationLog);

    } else if (communicationLog instanceof HttpCommunicationLog) {
      return ((HttpCommunicationLog) communicationLog).getRequest().toRawString(MAXIMUM_MESSAGE_LINE_LENGTH);

    } else {
      throw new IllegalArgumentException(
              "Unsupported communication log type: " + communicationLog.getClass().getSimpleName());
    }
  }

  private String getRequestAsString(HostPluginFunctionCallCommunicationLog hostPluginFunctionCallCommunicationLog) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(hostPluginFunctionCallCommunicationLog.getClassName());
    stringBuilder.append(".");
    stringBuilder.append(hostPluginFunctionCallCommunicationLog.getMethod());
    stringBuilder.append("(");
    stringBuilder.append(hostPluginFunctionCallCommunicationLog.getSortedArguments().stream()
            .map(FunctionCallArgumentLog::getValue).collect(Collectors.joining(", ")));
    stringBuilder.append(")");
    return stringBuilder.toString();
  }

  private String getResponseAsString(CommunicationLog communicationLog) {
    if (communicationLog instanceof HostPluginFunctionCallCommunicationLog) {
      return ((HostPluginFunctionCallCommunicationLog) communicationLog).getResult();

    } else if (communicationLog instanceof HttpCommunicationLog) {
      return ((HttpCommunicationLog) communicationLog).getResponse().toRawString(MAXIMUM_MESSAGE_LINE_LENGTH);

    } else {
      throw new IllegalArgumentException(
              "Unsupported communication log type: " + communicationLog.getClass().getSimpleName());
    }
  }

  private String getRequesterName(CommunicationLog communicationLog) {
    if (communicationLog.getParentCommunication() != null) {
      return getEntityName(communicationLog.getParentCommunication());
    }

    if (communicationLog instanceof HttpCommunicationFromEwpNodeLog) {
      HttpCommunicationFromEwpNodeLog httpCommunicationFromEwpNodeLog = (HttpCommunicationFromEwpNodeLog) communicationLog;
      return String.join(", ", httpCommunicationFromEwpNodeLog.getHeiIdsCoveredByClient());
    }

    return "Requester";
  }

  private String getEntityName(CommunicationLog communicationLog) {
    if (communicationLog instanceof HostPluginFunctionCallCommunicationLog) {
      HostPluginFunctionCallCommunicationLog hostPluginFunctionCallCommunicationLog = (HostPluginFunctionCallCommunicationLog) communicationLog;
      return hostPluginFunctionCallCommunicationLog.getHostPluginId();

    } else if (communicationLog instanceof HttpCommunicationFromHostLog) {
      HttpCommunicationFromHostLog httpCommunicationFromHostLog = (HttpCommunicationFromHostLog) communicationLog;
      return httpCommunicationFromHostLog.getHostForwardEwpApiClient().getId();

    } else if (communicationLog instanceof HttpCommunicationLog) {
      HttpCommunicationLog httpCommunicationLog = (HttpCommunicationLog) communicationLog;
      return httpCommunicationLog.getRequest().getUrl();

    } else {
      throw new IllegalArgumentException("Unknown communication log type: " + communicationLog.getClass().getSimpleName());
    }
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
    
    if (message == null) {
      message = "";
    }

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

  private void registerExceptionStacktrace(
          StringBuilder diagramBuilder, String placeAtRightOfAlias, String exceptionStacktrace) {
    // NOTE: the generation of the diagram fails if a line ends in whitespace (e.g. <br>), therefore
    // a blank character (#8203;) is used.
    String sanitizedMessage =
            exceptionStacktrace.replace(";", "#59;").replace(System.lineSeparator(), "<br>#8203;");

    diagramBuilder
            .append("\t")
            .append("Note right of ")
            .append(placeAtRightOfAlias)
            .append(": ")
            .append(sanitizedMessage)
            .append(System.lineSeparator());
  }
}
