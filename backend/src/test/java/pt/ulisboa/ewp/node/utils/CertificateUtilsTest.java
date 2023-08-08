package pt.ulisboa.ewp.node.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.junit.jupiter.api.Test;

public class CertificateUtilsTest {

  @Test
  public void testParseCertificateGivenValidCertificate() throws CertificateException {
    String certificate = "-----BEGIN CERTIFICATE-----\n"
        + "MIICtDCCAZygAwIBAgIJAPfMzBoYfXegMA0GCSqGSIb3DQEBBQUAMA8xDTALBgNV\n"
        + "BAMTBGRlbW8wHhcNMjEwMzIyMTYxNDM3WhcNMzEwMzIwMTYxNDM3WjAPMQ0wCwYD\n"
        + "VQQDEwRkZW1vMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsLKm/2pe\n"
        + "Kh691M/TYoh3QcbBdlUXTEVDSShG+9JAgchU7LNdTKr7+/1QK3YCRmMk7v3wWfXl\n"
        + "RaY3kgUCV/4FhWs+A0YRqOCXBEcKCAyK7Nu/sevvKt2gem6pdpGOscitKMSusoNx\n"
        + "FX/AKGe0DF1pWKmrh0QvZCtBK8cg5NkHvy2ON3Eq3pXTQHEjC3uJJnajNVVeIoIi\n"
        + "k2yhrGbBCuSXQo9keuM9XLCha4X8KUiURdlOqWBpXUYwW6RLEU5faefwI3eoSE+B\n"
        + "jCMvzMly6n+7cto5YdAUacVZyr/yq226uBYnfNYKlx2nRxkhMBH8lBtO51DvUJo8\n"
        + "rOeeLSHdnzyhVQIDAQABoxMwETAPBgNVHREECDAGggRkZW1vMA0GCSqGSIb3DQEB\n"
        + "BQUAA4IBAQBer7aE0J/TTAcdgvqqvtF+NUag/tfewFjeitzvABKzZ+2BDoXW8lKf\n"
        + "0PWlKfHR68N5Iq8CNZNso1IGgnmnzDvzW+u/fLB+6bP2FXT5U3168bVguzhfbZT/\n"
        + "jliJSv+wHnGk6ZDpw2KUVEJ2HJsmTZM4m3m0t49EifdFbCgF1eMXbylzihXCnvrk\n"
        + "c97y3B5tP5AayW0k9E9DsH14MR66+ANNwwTEJ3gyOZvJ9aBLHfHNN0OYjfeX/XDK\n"
        + "l9ZrBdnDA/gSnmBDcuuNXy9jFp+TUQM9A/jPme4Sam61l7O9RqLMNh3MKgYlXvLv\n"
        + "LZ41SfWl2lITNmJleyY9P87MxFWt8Hvu\n"
        + "-----END CERTIFICATE-----\n";
    assertThat(CertificateUtils.parseCertificate(certificate.getBytes(StandardCharsets.UTF_8))
        .getSubjectDN().getName())
        .isEqualTo("CN=demo");
  }

  @Test
  public void testExtractFingerprintGivenValidCertificate() throws CertificateException {
    String certificate = "-----BEGIN CERTIFICATE-----\n"
        + "MIICtDCCAZygAwIBAgIJAPfMzBoYfXegMA0GCSqGSIb3DQEBBQUAMA8xDTALBgNV\n"
        + "BAMTBGRlbW8wHhcNMjEwMzIyMTYxNDM3WhcNMzEwMzIwMTYxNDM3WjAPMQ0wCwYD\n"
        + "VQQDEwRkZW1vMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsLKm/2pe\n"
        + "Kh691M/TYoh3QcbBdlUXTEVDSShG+9JAgchU7LNdTKr7+/1QK3YCRmMk7v3wWfXl\n"
        + "RaY3kgUCV/4FhWs+A0YRqOCXBEcKCAyK7Nu/sevvKt2gem6pdpGOscitKMSusoNx\n"
        + "FX/AKGe0DF1pWKmrh0QvZCtBK8cg5NkHvy2ON3Eq3pXTQHEjC3uJJnajNVVeIoIi\n"
        + "k2yhrGbBCuSXQo9keuM9XLCha4X8KUiURdlOqWBpXUYwW6RLEU5faefwI3eoSE+B\n"
        + "jCMvzMly6n+7cto5YdAUacVZyr/yq226uBYnfNYKlx2nRxkhMBH8lBtO51DvUJo8\n"
        + "rOeeLSHdnzyhVQIDAQABoxMwETAPBgNVHREECDAGggRkZW1vMA0GCSqGSIb3DQEB\n"
        + "BQUAA4IBAQBer7aE0J/TTAcdgvqqvtF+NUag/tfewFjeitzvABKzZ+2BDoXW8lKf\n"
        + "0PWlKfHR68N5Iq8CNZNso1IGgnmnzDvzW+u/fLB+6bP2FXT5U3168bVguzhfbZT/\n"
        + "jliJSv+wHnGk6ZDpw2KUVEJ2HJsmTZM4m3m0t49EifdFbCgF1eMXbylzihXCnvrk\n"
        + "c97y3B5tP5AayW0k9E9DsH14MR66+ANNwwTEJ3gyOZvJ9aBLHfHNN0OYjfeX/XDK\n"
        + "l9ZrBdnDA/gSnmBDcuuNXy9jFp+TUQM9A/jPme4Sam61l7O9RqLMNh3MKgYlXvLv\n"
        + "LZ41SfWl2lITNmJleyY9P87MxFWt8Hvu\n"
        + "-----END CERTIFICATE-----\n";
    X509Certificate x509Certificate = CertificateUtils
        .parseCertificate(certificate.getBytes(StandardCharsets.UTF_8));
    assertThat(CertificateUtils.extractFingerprint(x509Certificate))
        .isEqualTo("343076d0b44949bcc3f288c72d841bc11a53b4ffc7aa206320e6851bdac1023f");
  }

}
