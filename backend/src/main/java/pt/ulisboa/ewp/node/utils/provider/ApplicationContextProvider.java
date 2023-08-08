package pt.ulisboa.ewp.node.utils.provider;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextProvider implements ApplicationContextAware {

  private static ApplicationContext applicationContext;

  public static synchronized ApplicationContext getApplicationContext() {
    return applicationContext;
  }

  @Override
  public synchronized void setApplicationContext(ApplicationContext applicationContext)
      throws BeansException {
    ApplicationContextProvider.applicationContext = applicationContext;
  }
}
