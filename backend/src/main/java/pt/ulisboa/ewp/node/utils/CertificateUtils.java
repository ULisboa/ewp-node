package pt.ulisboa.ewp.node.utils;

import jakarta.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Locale;

public class CertificateUtils {

  private CertificateUtils() {
  }

  public static X509Certificate parseCertificate(byte[] certificateBytes)
      throws CertificateException {
    CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
    ByteArrayInputStream certificateInputStream = new ByteArrayInputStream(certificateBytes);
    return (X509Certificate) certificateFactory.generateCertificate(certificateInputStream);
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
}
