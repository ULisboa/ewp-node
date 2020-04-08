package pt.ulisboa.ewp.node.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

  private DateUtils() {}

  public static String toStringAsGMT(Date date, String format) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    return simpleDateFormat.format(date);
  }
}
