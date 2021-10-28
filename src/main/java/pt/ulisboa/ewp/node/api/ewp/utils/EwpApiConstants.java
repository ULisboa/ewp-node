package pt.ulisboa.ewp.node.api.ewp.utils;

public class EwpApiConstants {

  private EwpApiConstants() {
  }

  public static final String API_BASE_URI = "/api/ewp/";

  /**
   * Should be used only for exceptional backwards compatibility.
   */
  public static final String REST_BASE_URI = "/rest/ewp/";

  public static final String DISCOVERY_VERSION = "5.0.0";
  public static final String ECHO_VERSION = "2.0.1";

  public static final int MAX_HEI_IDS = 1;

  public static final String API_INSTITUTIONS_LOCAL_NAME = "institutions";
  public static final String API_ORGANIZATIONAL_UNITS_NAME = "organizational-units";
  public static final String API_COURSES_NAME = "courses";
  public static final String API_FACTSHEETS_NAME = "factsheet";
  public static final String API_OUTGOING_MOBILITIES_NAME = "omobilities";
  public static final String API_OUTGOING_MOBILITY_CNR_NAME = "omobility-cnr";
  public static final String API_OUTGOING_MOBILITY_LEARNING_AGREEMENTS_NAME = "omobility-las";
  public static final String API_OUTGOING_MOBILITY_LEARNING_AGREEMENT_CNR_NAME = "omobility-la-cnr";
  public static final String API_SIMPLE_COURSE_REPLICATION_NAME = "simple-course-replication";
  public static final String API_INTERINSTITUTIONAL_AGREEMENTS_NAME = "iias";
  public static final String API_INTERINSTITUTIONAL_AGREEMENT_CNR_NAME = "iia-cnr";
  public static final String API_INTERINSTITUTIONAL_AGREEMENTS_APPROVAL_NAME = "iias-approval";
  public static final String API_INTERINSTITUTIONAL_AGREEMENT_APPROVAL_CNR_NAME = "iia-approval-cnr";
  public static final String API_INCOMING_MOBILITIES_NAME = "imobilities";
  public static final String API_INCOMING_MOBILITY_CNR_NAME = "imobility-cnr";
  public static final String API_INCOMING_MOBILITY_TORS_CNR_NAME = "imobility-tors";
  public static final String API_INCOMING_MOBILITY_TOR_CNR_NAME = "imobility-tor-cnr";
}
