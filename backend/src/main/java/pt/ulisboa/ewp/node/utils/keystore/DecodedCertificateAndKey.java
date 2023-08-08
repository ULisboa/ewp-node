package pt.ulisboa.ewp.node.utils.keystore;

import java.security.Key;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class DecodedCertificateAndKey {

  private Certificate certificate;

  private Key privateKey;
  private Key rsaPublicKey;

  private String formattedRsaPublicKey;
  private String publicKeyFingerprint;
  private String formattedCertificate;

  public DecodedCertificateAndKey(Certificate certificate, Key privateKey)
      throws CertificateEncodingException {
    if (certificate == null) {
      throw new IllegalArgumentException("Certificate cannot be null");
    }

    if (privateKey == null) {
      throw new IllegalArgumentException("Private Key cannot be null");
    }

    this.certificate = certificate;
    this.privateKey = privateKey;
    parseCertificate();
  }

  private void parseCertificate() throws CertificateEncodingException {
    byte[] publicKey = Base64.getEncoder().encode(certificate.getPublicKey().getEncoded());
    this.rsaPublicKey = certificate.getPublicKey();
    this.formattedRsaPublicKey = new String(publicKey).replaceAll("(.{1,64})", "$1\n");

    this.publicKeyFingerprint = DigestUtils.sha256Hex(certificate.getPublicKey().getEncoded());

    byte[] cert = Base64.getEncoder().encode(certificate.getEncoded());
    this.formattedCertificate = new String(cert).replaceAll("(.{1,64})", "$1\n");
  }

  public String getFormattedCertificate() {
    return this.formattedCertificate;
  }

  public Certificate getCertificate() {
    return certificate;
  }

  public Key getPrivateKey() {
    return privateKey;
  }

  public Key getRsaPublicKey() {
    return this.rsaPublicKey;
  }

  public String getFormattedRsaPublicKey() {
    return formattedRsaPublicKey;
  }

  public String getPublicKeyFingerprint() {
    return this.publicKeyFingerprint;
  }
}
