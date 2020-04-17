package pt.ulisboa.ewp.node.exception;

public class XmlCannotUnmarshallToTypeException extends Exception {

  private String xml;
  private Class<?> classType;

  public XmlCannotUnmarshallToTypeException(String xml, Class classType) {
    this.xml = xml;
    this.classType = classType;
  }

  public String getXml() {
    return xml;
  }

  public Class<?> getClassType() {
    return classType;
  }

  @Override
  public String getMessage() {
    return "XML cannot be cast to " + classType.getCanonicalName();
  }
}
