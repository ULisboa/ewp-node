package pt.ulisboa.ewp.node.exception.keystore;

public class KeysDoNotMatchException extends Exception {

  public KeysDoNotMatchException() {
    super("Keys do not match");
  }
}
