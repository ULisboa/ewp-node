package pt.ulisboa.ewp.node.utils.http.converter.xml;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Namespace prefix mapper that maps EWP namespaces into predefined prefixes.
 */
public class EwpNamespacePrefixMapper extends NamespacePrefixMapper {

  private static final Pattern PATTERN_IIAS_GET_RESPONSE = Pattern.compile(
      "https://github.com/erasmus-without-paper/ewp-specs-api-iias/blob/stable-v[0-9]+/endpoints/get-response.xsd");

  private static final Pattern PATTERN_SPECS_TYPES_CONTACT = Pattern.compile(
      "https://github.com/erasmus-without-paper/ewp-specs-types-contact/tree/stable-v[0-9]+");

  private static final Pattern PATTERN_SPECS_TYPES_ADDRESS = Pattern.compile(
      "https://github.com/erasmus-without-paper/ewp-specs-types-address/tree/stable-v[0-9]+");

  private static final Pattern PATTERN_SPECS_TYPES_PHONE_NUMBER = Pattern.compile(
      "https://github.com/erasmus-without-paper/ewp-specs-types-phonenumber/tree/stable-v[0-9]+");

  private final Map<Pattern, String> patternToPrefixMap = new HashMap<>();

  public EwpNamespacePrefixMapper() {
    this.patternToPrefixMap.put(PATTERN_IIAS_GET_RESPONSE, "");
    this.patternToPrefixMap.put(PATTERN_SPECS_TYPES_CONTACT, "c");
    this.patternToPrefixMap.put(PATTERN_SPECS_TYPES_ADDRESS, "a");
    this.patternToPrefixMap.put(PATTERN_SPECS_TYPES_PHONE_NUMBER, "pn");
  }

  @Override
  public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
    for (Map.Entry<Pattern, String> entry : this.patternToPrefixMap.entrySet()) {
      Pattern pattern = entry.getKey();
      String prefix = entry.getValue();
      if (pattern.matcher(namespaceUri).matches()) {
        return prefix;
      }
    }
    return null;
  }
}
