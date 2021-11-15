package pt.ulisboa.ewp.node.validation.validator;

import java.util.Arrays;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.utils.EwpApi;
import pt.ulisboa.ewp.node.validation.annotation.ValidEwpApiLocalName;

@Component
public class ValidEwpApiLocalNameValidator implements
    ConstraintValidator<ValidEwpApiLocalName, String> {

  @Override
  public void initialize(ValidEwpApiLocalName constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

    constraintValidatorContext.disableDefaultConstraintViolation();

    if (value == null || EwpApi.findByLocalName(value).isEmpty()) {
      addConstraintViolation(constraintValidatorContext);
      return false;
    }

    return true;
  }

  private void addConstraintViolation(ConstraintValidatorContext constraintValidatorContext) {
    constraintValidatorContext.buildConstraintViolationWithTemplate("must be one of: " + Arrays
        .toString(EwpApi.getApiLocalNames())).addConstraintViolation();
  }
}
