package pt.ulisboa.ewp.node.service.communication.log.http.validator;

import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.domain.dto.communication.log.http.HttpResponseLogDto;
import pt.ulisboa.ewp.node.domain.dto.validation.ValidationEntryDto;
import pt.ulisboa.ewp.node.domain.dto.validation.ValidationEntryDto.Severity;
import pt.ulisboa.ewp.node.domain.dto.validation.ValidationResultDto;
import pt.ulisboa.ewp.node.service.xml.XmlValidator;

@Service
public class HttpResponseLogBodyValidator {

  private final XmlValidator xmlValidator;

  public HttpResponseLogBodyValidator(XmlValidator xmlValidator) {
    this.xmlValidator = xmlValidator;
  }

  public ValidationResultDto validate(HttpResponseLogDto httpResponse) {
    byte[] responseBodyBytes = httpResponse.getBody().getBytes(StandardCharsets.UTF_8);
    try {
      return this.xmlValidator.validate(responseBodyBytes);
    } catch (Exception e) {
      return new ValidationResultDto(
          List.of(
              new ValidationEntryDto(
                  Severity.WARNING, "Validator failure or unknown response body type")));
    }
  }
}
