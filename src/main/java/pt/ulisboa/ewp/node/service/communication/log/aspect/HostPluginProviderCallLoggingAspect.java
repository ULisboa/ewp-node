package pt.ulisboa.ewp.node.service.communication.log.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class HostPluginProviderCallLoggingAspect {

  private static final Logger LOG =
      LoggerFactory.getLogger(HostPluginProviderCallLoggingAspect.class);

  @Before("execution(* pt.ulisboa.ewp.host.plugin.skeleton.provider.HostProvider+.*(..))")
  public void before(JoinPoint joinPoint) throws Throwable {
    // TODO log host function calls
  }
}
