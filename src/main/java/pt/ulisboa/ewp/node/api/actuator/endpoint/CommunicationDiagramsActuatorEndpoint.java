package pt.ulisboa.ewp.node.api.actuator.endpoint;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.transaction.Transactional;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pt.ulisboa.ewp.node.domain.entity.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.entity.http.log.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.http.log.host.HttpCommunicationFromHostLog;
import pt.ulisboa.ewp.node.domain.repository.http.log.HttpCommunicationLogRepository;

@Component
@RestControllerEndpoint(id = "communications")
public class CommunicationDiagramsActuatorEndpoint {

  private static final Logger LOG = LoggerFactory.getLogger(
      CommunicationDiagramsActuatorEndpoint.class);

  private static final String DIAGRAM_OUTPUT_FORMAT = "svg";
  private static final int MAXIMUM_MESSAGE_LINE_LENGTH = 60;

  private final HttpCommunicationLogRepository repository;

  public CommunicationDiagramsActuatorEndpoint(HttpCommunicationLogRepository repository) {
    this.repository = repository;
  }

  // Final URI: /actuator/communications/diagrams/{id}
  @GetMapping("/diagrams/{id}")
  @Transactional
  public byte[] getDiagramForCommunication(@PathVariable("id") long id)
      throws IOException, InterruptedException {
    Optional<HttpCommunicationLog> communicationLogOptional = repository.findById(id);
    if (communicationLogOptional.isEmpty()) {
      return null;
    }
    HttpCommunicationLog communicationLog = communicationLogOptional.get();
    String sequenceDiagramString = createSequenceDiagramInMermaidFormat(communicationLog);
    return generateDiagram(sequenceDiagramString);
  }

  private byte[] generateDiagram(CharSequence sequenceDiagramString) {
    File inputTempFile = null;
    File outputTempFile = null;
    try {
      inputTempFile = File.createTempFile("ewp-node-communication-diagram-", ".in");
      LOG.debug("Preparing to write communication diagram to temporary file: {}",
          inputTempFile.getAbsolutePath());
      FileUtils.write(inputTempFile, sequenceDiagramString, "UTF-8");

      outputTempFile = File.createTempFile("ewp-node-communication-diagram-",
          "." + DIAGRAM_OUTPUT_FORMAT);

      Process process = Runtime.getRuntime().exec(
          "mmdc -i " + inputTempFile.getAbsolutePath() + " -o " + outputTempFile.getAbsolutePath()
              + " -p " + "/opt/puppeteer-config.json");
      process.waitFor();

      if (process.exitValue() != 0) {
        String error = new String(process.getErrorStream().readAllBytes());
        throw new IllegalStateException("Diagram generation failed: \"" + error + "\"");
      }

      return FileUtils.readFileToByteArray(outputTempFile);

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

  private String createSequenceDiagramInMermaidFormat(HttpCommunicationLog communicationLog) {
    StringBuilder diagramBuilder = new StringBuilder();
    diagramBuilder.append("sequenceDiagram").append(System.lineSeparator());
    Map<String, String> participantToAliasMap = new HashMap<>();
    fillBuilderWithSequenceDiagramInMermaidFormat(diagramBuilder, communicationLog,
        participantToAliasMap);
    return diagramBuilder.toString();
  }

  private void fillBuilderWithSequenceDiagramInMermaidFormat(StringBuilder diagramBuilder,
      HttpCommunicationLog communicationLog, Map<String, String> participantToAliasMap) {

    HttpRequestLog request = communicationLog.getRequest();
    String requesterName = getRequesterName(communicationLog);
    registerParticipantIfMissing(diagramBuilder, participantToAliasMap, requesterName);
    String requesterAlias = participantToAliasMap.get(requesterName);
    String receiverName = request.getUrl();
    registerParticipantIfMissing(diagramBuilder, participantToAliasMap, receiverName);
    String receiverAlias = participantToAliasMap.get(receiverName);
    registerCommunication(diagramBuilder, requesterAlias, receiverAlias, "->>",
        request.toRawString(MAXIMUM_MESSAGE_LINE_LENGTH));

    for (HttpCommunicationLog childCommunicationLog : communicationLog.getSortedChildrenCommunications()) {
      fillBuilderWithSequenceDiagramInMermaidFormat(diagramBuilder, childCommunicationLog,
          participantToAliasMap);
    }

    HttpResponseLog response = communicationLog.getResponse();
    registerCommunication(diagramBuilder, receiverAlias, requesterAlias, "-->>",
        response.toRawString(MAXIMUM_MESSAGE_LINE_LENGTH));
  }

  private String getRequesterName(HttpCommunicationLog communicationLog) {
    if (communicationLog.getParentCommunication() != null) {
      return communicationLog.getParentCommunication().getRequest().getUrl();
    }

    if (communicationLog instanceof HttpCommunicationFromHostLog) {
      HttpCommunicationFromHostLog communicationFromHostLog = (HttpCommunicationFromHostLog) communicationLog;
      if (communicationFromHostLog.getHostForwardEwpApiClient() != null) {
        return communicationFromHostLog.getHostForwardEwpApiClient().getId();
      }
    }

    return "Requester";
  }

  private void registerParticipantIfMissing(StringBuilder diagramBuilder,
      Map<String, String> participantToAliasMap,
      String participant) {
    if (!participantToAliasMap.containsKey(participant)) {
      String nextAlias = "p" + (participantToAliasMap.size() + 1);
      participantToAliasMap.put(participant, nextAlias);
      diagramBuilder.append("\tparticipant ").append(nextAlias).append(" as ").append(participant)
          .append(System.lineSeparator());
    }
  }

  private void registerCommunication(StringBuilder diagramBuilder, String requesterAlias,
      String receiverAlias, String arrowType, String message) {
    String sanitizedMessage = message.replace(";", "#59;").replace(System.lineSeparator(), "<br>");

    diagramBuilder.append("\t").append(requesterAlias).append(" ").append(arrowType).append(" ")
        .append(receiverAlias)
        .append(": ")
        .append(sanitizedMessage)
        .append(System.lineSeparator());
  }
}
