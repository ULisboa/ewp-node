package pt.ulisboa.ewp.node.utils.i18n;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.utils.provider.ApplicationContextProvider;

@Component
public class MessageResolver {

  @Autowired private Logger logger;

  @Autowired private MessageSource messageSource;

  public static MessageResolver getInstance() {
    return ApplicationContextProvider.getApplicationContext().getBean(MessageResolver.class);
  }

  public String get(String code) {
    return get(code, new String[0]);
  }

  public String get(MessageSourceResolvable messageSourceResolvable) {
    try {
      return messageSource.getMessage(messageSourceResolvable, LocaleContextHolder.getLocale());
    } catch (NoSuchMessageException exception) {
      logger.warn(exception.getMessage());
      return "";
    }
  }

  public String get(String code, String... args) {
    try {
      return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    } catch (NoSuchMessageException exception) {
      logger.warn(exception.getMessage());
      return "";
    }
  }
}
