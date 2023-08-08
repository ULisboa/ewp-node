package pt.ulisboa.ewp.node.utils.serialization;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SerializationUtilsTest {

  @Test
  public void testConvertToTypeAndString_String_ReturnsCorrectTypeAndString() {
    String object = "test 123";

    TypeAndString typeAndString = SerializationUtils.convertToTypeAndString(object);

    assertThat(typeAndString).isEqualTo(new TypeAndString("String", "\"test 123\""));
  }

  @Test
  public void testConvertToTypeAndString_Integer_ReturnsCorrectTypeAndString() {
    int object = 123;

    TypeAndString typeAndString = SerializationUtils.convertToTypeAndString(object);

    assertThat(typeAndString).isEqualTo(new TypeAndString("Number", "123"));
  }

  @Test
  public void testConvertToTypeAndString_Double_ReturnsCorrectTypeAndString() {
    double object = 123.567;

    TypeAndString typeAndString = SerializationUtils.convertToTypeAndString(object);

    assertThat(typeAndString).isEqualTo(new TypeAndString("Number", "123.567"));
  }

  @Test
  public void testConvertToTypeAndString_LocalDate_ReturnsCorrectTypeAndString() {
    LocalDate object = LocalDate.of(2025, 6, 10);

    TypeAndString typeAndString = SerializationUtils.convertToTypeAndString(object);

    assertThat(typeAndString).isEqualTo(new TypeAndString("LocalDate", "2025-06-10"));
  }

  @Test
  public void testConvertToTypeAndString_LocalDateTime_ReturnsCorrectTypeAndString() {
    LocalDateTime object = LocalDateTime.of(2025, 6, 10, 15, 13, 11);

    TypeAndString typeAndString = SerializationUtils.convertToTypeAndString(object);

    assertThat(typeAndString).isEqualTo(new TypeAndString("LocalDateTime", "2025-06-10T15:13:11"));
  }

  @Test
  public void testConvertToTypeAndString_CollectionOfTwoStrings_ReturnsCorrectTypeAndString() {
    Collection<String> object = List.of("test 123", "qwerty");

    TypeAndString typeAndString = SerializationUtils.convertToTypeAndString(object);

    assertThat(typeAndString).isEqualTo(new TypeAndString("Collection<String>", "[\"test 123\", \"qwerty\"]"));
  }

  @Test
  public void testConvertToTypeAndString_OptionalWithString_ReturnsCorrectTypeAndString() {
    Optional<String> object = Optional.of("test 123");

    TypeAndString typeAndString = SerializationUtils.convertToTypeAndString(object);

    assertThat(typeAndString).isEqualTo(new TypeAndString("Optional<String>", "Optional.of(\"test 123\")"));
  }

  @Test
  public void testConvertToTypeAndString_OptionalEmpty_ReturnsCorrectTypeAndString() {
    Optional<?> object = Optional.empty();

    TypeAndString typeAndString = SerializationUtils.convertToTypeAndString(object);

    assertThat(typeAndString).isEqualTo(new TypeAndString("Optional<?>", "Optional.empty()"));
  }
}
