package pt.ulisboa.ewp.node.utils;

import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public enum EwpApi {
  INSTITUTIONS("institutions"),
  ORGANIZATIONAL_UNITS("organizational-units"),
  COURSES("courses"),
  SIMPLE_COURSE_REPLICATION("simple-course-replication"),
  FILES("file"),
  FACTSHEETS("factsheet"),
  INTERINSTITUTIONAL_AGREEMENTS("iias"),
  INTERINSTITUTIONAL_AGREEMENT_CNR("iia-cnr"),
  INTERINSTITUTIONAL_AGREEMENTS_APPROVAL("iias-approval"),
  INTERINSTITUTIONAL_AGREEMENTS_APPROVAL_CNR("iia-approval-cnr"),
  OUTGOING_MOBILITIES("omobilities"),
  OUTGOING_MOBILITY_CNR("omobility-cnr"),
  OUTGOING_MOBILITY_LEARNING_AGREEMENTS("omobility-las"),
  OUTGOING_MOBILITY_LEARNING_AGREEMENT_CNR("omobility-la-cnr"),
  INCOMING_MOBILITIES("imobilities"),
  INCOMING_MOBILITY_CNR("imobility-cnr"),
  INCOMING_MOBILITY_TORS("imobility-tors"),
  INCOMING_MOBILITY_TOR_CNR("imobility-tor-cnr"),
  MONITORING("monitoring");

  private final String localName;

  EwpApi(String localName) {
    this.localName = localName;
  }

  public String getLocalName() {
    return localName;
  }

  public static Optional<EwpApi> findByLocalName(String localName) {
    return Arrays.stream(values()).filter(v -> StringUtils.equalsIgnoreCase(v.localName, localName))
        .findFirst();
  }

  public static String[] getApiLocalNames() {
    return Arrays.stream(values()).map(v -> v.localName).toArray(String[]::new);
  }
}
