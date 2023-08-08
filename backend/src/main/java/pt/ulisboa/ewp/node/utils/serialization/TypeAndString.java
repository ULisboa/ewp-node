package pt.ulisboa.ewp.node.utils.serialization;

import java.util.Objects;

public class TypeAndString {

  private final String type;
  private final String string;

  public TypeAndString(String type, String string) {
    this.type = type;
    this.string = string;
  }

  public String getType() {
    return type;
  }

  public String getString() {
    return string;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;
    TypeAndString that = (TypeAndString) object;
    return Objects.equals(type, that.type) && Objects.equals(string, that.string);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, string);
  }

  @Override
  public String toString() {
    return type + ": " + string;
  }
}
