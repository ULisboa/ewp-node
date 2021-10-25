package pt.ulisboa.ewp.node.utils;

import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;

public enum EwpApiLocalName {
  INSTITUTIONS(EwpApiConstants.API_INSTITUTIONS_LOCAL_NAME),
  ORGANIZATIONAL_UNITS(EwpApiConstants.API_ORGANIZATIONAL_UNITS_NAME),
  COURSES(EwpApiConstants.API_COURSES_NAME),
  SIMPLE_COURSE_REPLICATION(EwpApiConstants.API_SIMPLE_COURSE_REPLICATION_NAME),
  INTERINSTITUTIONAL_AGREEMENTS(EwpApiConstants.API_INTERINSTITUTIONAL_AGREEMENTS_NAME),
  INTERINSTITUTIONAL_AGREEMENTS_APPROVAL(
      EwpApiConstants.API_INTERINSTITUTIONAL_AGREEMENTS_APPROVAL_NAME),
  OUTGOING_MOBILITIES(EwpApiConstants.API_OUTGOING_MOBILITIES_NAME),
  OUTGOING_MOBILITY_CNR(EwpApiConstants.API_OUTGOING_MOBILITY_CNR_NAME),
  OUTGOING_MOBILITY_LEARNING_AGREEMENTS(
      EwpApiConstants.API_OUTGOING_MOBILITY_LEARNING_AGREEMENTS_NAME),
  OUTGOING_MOBILITY_LEARNING_AGREEMENT_CNR(
      EwpApiConstants.API_OUTGOING_MOBILITY_LEARNING_AGREEMENT_CNR_NAME),
  INCOMING_MOBILITIES(EwpApiConstants.API_INCOMING_MOBILITIES_NAME);

  private final String localName;

  EwpApiLocalName(String localName) {
    this.localName = localName;
  }

  public String getLocalName() {
    return localName;
  }

  public static Optional<EwpApiLocalName> findByLocalName(String localName) {
    return Arrays.stream(values()).filter(v -> StringUtils.equalsIgnoreCase(v.localName, localName))
        .findFirst();
  }

  public static String[] getApiLocalNames() {
    return Arrays.stream(values()).map(v -> v.localName).toArray(String[]::new);
  }
}
