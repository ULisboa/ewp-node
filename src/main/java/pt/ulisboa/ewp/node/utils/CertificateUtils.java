package pt.ulisboa.ewp.node.utils;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Locale;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.ewp.node.config.security.SecurityClientTlsEncoding;

public class CertificateUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(CertificateUtils.class);

  private static final String BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----";
  private static final String END_CERTIFICATE = "-----END CERTIFICATE-----";

  private CertificateUtils() {
  }

  public static X509Certificate parseCertificate(byte[] certificateBytes)
      throws CertificateException {
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    ByteArrayInputStream certificateInputStream = new ByteArrayInputStream(certificateBytes);
    return (X509Certificate) certificateFactory.generateCertificate(certificateInputStream);
  }

  public static X509Certificate decodeCertificate(
      String certificateString, SecurityClientTlsEncoding encoding)
      throws CertificateException, DecoderException {
    String sanitizedCertificateString =
        certificateString.replace(BEGIN_CERTIFICATE, "").replace(END_CERTIFICATE, "");
    byte[] decodedCertificate;
    switch (encoding) {
      case BASE64:
        decodedCertificate = Base64.decodeBase64(sanitizedCertificateString);
        break;

      case HEX:
        decodedCertificate = Hex.decodeHex(sanitizedCertificateString);
        break;

      default:
        throw new IllegalStateException("Unknown encoding: " + encoding);
    }
    return (X509Certificate)
        CertificateFactory.getInstance("X.509")
            .generateCertificate(new ByteArrayInputStream(decodedCertificate));
  }

  public static String extractFingerprint(Certificate cert) {
    MessageDigest md;
    try {
      md = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
    try {
      byte[] encoded = cert.getEncoded();
      if (encoded == null) {
        return null;
      }
      md.update(encoded);
    } catch (CertificateEncodingException e) {
      throw new IllegalStateException(e);
    }
    byte[] binDigest = md.digest();
    return DatatypeConverter.printHexBinary(binDigest).toLowerCase(Locale.ENGLISH);
  }

  public static void logCertificates(X509Certificate[] certificates) {
    if (certificates != null) {
      LOGGER.info("Certificates: ");
      for (X509Certificate certificate : certificates) {
        logCertificate(certificate);
      }
    }
  }

  public static void logCertificate(X509Certificate certificate) {
    String fingerprint = CertificateUtils.extractFingerprint(certificate);
    if (certificate.getSubjectDN() != null) {
      LOGGER.info("\t- {}: (Subject DN: {})", fingerprint, certificate.getSubjectDN().getName());
    } else {
      LOGGER.info("\t- {}: (Without subject DN)", fingerprint);
    }
  }
}
