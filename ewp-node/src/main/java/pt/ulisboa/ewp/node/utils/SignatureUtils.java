package pt.ulisboa.ewp.node.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.UUID;

public class SignatureUtils {

  public static boolean verifyKeysMatch(PublicKey publicKey, PrivateKey privateKey)
      throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
    byte[] data = UUID.randomUUID().toString().getBytes();
    byte[] signed = sign(data, privateKey);
    return verify(data, publicKey, signed);
  }

  public static byte[] sign(byte[] data, PrivateKey privateKey)
      throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
    Signature signer = Signature.getInstance("SHA256withRSA");
    signer.initSign(privateKey);
    signer.update(data);
    return signer.sign();
  }

  public static boolean verify(byte[] data, PublicKey publicKey, byte[] signedData)
      throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
    Signature signer = Signature.getInstance("SHA256withRSA");
    signer.initVerify(publicKey);
    signer.update(data);
    return signer.verify(signedData);
  }
}
