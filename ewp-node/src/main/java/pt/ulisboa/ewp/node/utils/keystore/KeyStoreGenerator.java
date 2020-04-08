package pt.ulisboa.ewp.node.utils.keystore;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class KeyStoreGenerator {

  public static DecodedKeystore generate(String keyStorePassword, String certificateAlias)
      throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException,
          NoSuchProviderException, OperatorCreationException, UnrecoverableKeyException {
    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    keyStore.load(null, keyStorePassword.toCharArray());

    generateKeystoreCertificate(keyStore, certificateAlias, keyStorePassword);

    return new DecodedKeystore(keyStore, keyStorePassword);
  }

  public static DecodedKeystore generateFromCertificateAndKey(
      String keyStorePassword,
      String certificateAlias,
      X509Certificate certificate,
      PrivateKey privateKey)
      throws KeyStoreException, UnrecoverableKeyException, CertificateException,
          NoSuchAlgorithmException, IOException {
    KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
    keyStore.load(null, keyStorePassword.toCharArray());

    KeyStoreUtil.setKeyStoreCertificate(
        keyStore, keyStorePassword, certificateAlias, certificate, privateKey);

    return new DecodedKeystore(keyStore, keyStorePassword);
  }

  private static void generateKeystoreCertificate(
      KeyStore keyStore, String certificateAlias, String password)
      throws NoSuchProviderException, NoSuchAlgorithmException, IOException, CertificateException,
          KeyStoreException, OperatorCreationException {
    Security.addProvider(new BouncyCastleProvider());

    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    keyPairGenerator.initialize(2048);
    KeyPair keyPair = keyPairGenerator.generateKeyPair();

    KeyStoreUtil.setKeyStoreCertificate(
        keyStore,
        password,
        certificateAlias,
        generateLocalhostCertificate(keyPair),
        keyPair.getPrivate());
  }

  private static X509Certificate generateLocalhostCertificate(KeyPair keyPair)
      throws IOException, OperatorCreationException, CertificateException, NoSuchProviderException {
    SubjectPublicKeyInfo subjectPublicKeyInfo =
        SubjectPublicKeyInfo.getInstance(
            ASN1Sequence.getInstance(keyPair.getPublic().getEncoded()));
    X509v3CertificateBuilder certificateBuilder =
        new X509v3CertificateBuilder(
            new X500Name("CN=localhost"),
            BigInteger.valueOf(123456789),
            new Date(System.currentTimeMillis()),
            new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60),
            new X500Name("CN=localhost"),
            subjectPublicKeyInfo);

    ContentSigner contentSigner =
        new JcaContentSignerBuilder("SHA1withRSA").build(keyPair.getPrivate());

    InputStream inputStream =
        new ByteArrayInputStream(
            certificateBuilder.build(contentSigner).toASN1Structure().getEncoded());
    CertificateFactory certificateFactory =
        CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
    return (X509Certificate) certificateFactory.generateCertificate(inputStream);
  }
}
