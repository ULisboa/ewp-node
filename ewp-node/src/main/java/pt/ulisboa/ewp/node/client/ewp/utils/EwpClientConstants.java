package pt.ulisboa.ewp.node.client.ewp.utils;

import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;

public class EwpClientConstants {

  public static final String QUERY_HEI_ID = "hei_id";
  public static final String QUERY_LOS_ID = "los_id";
  public static final String QUERY_LOS_CODE = "los_code";
  public static final String QUERY_LOIS_BEFORE = "lois_before";
  public static final String QUERY_LOIS_AFTER = "lois_after";
  public static final String QUERY_LOIS_AT_DATE = "los_at_date";
  public static final String QUERY_MODIFIED_SINCE = "modified_since";
  public static final String QUERY_ORGANIZATIONAL_UNIT_CODE = "ounit_code";
  public static final String QUERY_ORGANIZATIONAL_UNIT_ID = "ounit_id";

  public static final String API_INSTITUTIONS_LOCAL_NAME = "institutions";
  public static final String API_ORGANIZATIONAL_UNITS_NAME = "organizational-units";
  public static final String API_COURSES_NAME = "courses";
  public static final String API_SIMPLE_COURSE_REPLICATION_NAME = "simple-course-replication";

  public static final EwpAuthenticationMethod[] AUTHENTICATION_METHODS_BY_PREFERENTIAL_ORDER = {
    EwpAuthenticationMethod.HTTP_SIGNATURE,
    EwpAuthenticationMethod.TLS,
    EwpAuthenticationMethod.ANONYMOUS
  };
}
