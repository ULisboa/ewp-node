package pt.ulisboa.ewp.node.domain.dto.validation;

import javax.xml.bind.ValidationEvent;
import org.xml.sax.SAXParseException;

public class ValidationEntryDto {

  private final Severity severity;
  private final int lineNumber;
  private final String message;

  public ValidationEntryDto(Severity severity, SAXParseException ex) {
    this.severity = severity;
    this.lineNumber = ex.getLineNumber();
    this.message = ex.getMessage();
  }

  public ValidationEntryDto(Severity severity, String message) {
    this.severity = severity;
    this.lineNumber = 1;
    this.message = message;
  }

  public ValidationEntryDto(ValidationEvent validationEvent) {
    this.severity =
        validationEvent.getSeverity() != ValidationEvent.WARNING
            ? Severity.ERROR
            : Severity.WARNING;
    this.lineNumber = validationEvent.getLocator().getLineNumber();
    this.message = validationEvent.getMessage();
  }

  public Severity getSeverity() {
    return severity;
  }

  public int getLineNumber() {
    return this.lineNumber;
  }

  public String getMessage() {
    return this.message;
  }

  @Override
  public String toString() {
    return this.lineNumber + ": " + this.message;
  }

  public enum Severity {
    ERROR,
    WARNING
  }

}
