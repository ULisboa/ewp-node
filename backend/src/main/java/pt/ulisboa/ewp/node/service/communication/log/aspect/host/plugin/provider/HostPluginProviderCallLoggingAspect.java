package pt.ulisboa.ewp.node.service.communication.log.aspect.host.plugin.provider;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import pt.ulisboa.ewp.host.plugin.skeleton.HostPlugin;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.HostProvider;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.host.plugin.HostPluginFunctionCallCommunicationLog;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContext;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContextHolder;
import pt.ulisboa.ewp.node.service.communication.log.host.plugin.HostPluginFunctionCallCommunicationLogService;
import pt.ulisboa.ewp.node.utils.serialization.SerializationUtils;
import pt.ulisboa.ewp.node.utils.serialization.TypeAndString;

@Aspect
@Configuration
public class HostPluginProviderCallLoggingAspect extends HostPluginProviderAspect {

  private final HostPluginFunctionCallCommunicationLogService communicationLogService;

  public HostPluginProviderCallLoggingAspect(
      HostPluginFunctionCallCommunicationLogService communicationLogService) {
    this.communicationLogService = communicationLogService;
  }

  @Pointcut("execution(* pt.ulisboa.ewp.host.plugin.skeleton.provider.HostProvider+.*(..))")
  private void isHostProviderFunctionCall() {}

  @Pointcut("execution(* pt.ulisboa.ewp.host.plugin.skeleton.interfaces.PluginAware.*(..))")
  private void isPluginAwareFunctionCall() {}

  @Around("isHostProviderFunctionCall() && !isPluginAwareFunctionCall()")
  public Object onFunctionCall(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    return CommunicationContextHolder.runInNestedContext(
        (context) -> {
          HostPluginFunctionCallCommunicationLog communicationLog;
          HostProvider hostProvider = (HostProvider) proceedingJoinPoint.getTarget();
          try {
            communicationLog = createCommunicationLog(proceedingJoinPoint, context, hostProvider);
          } catch (DomainException e) {
            throw new IllegalStateException("Failed to prepare to process host plugin call", e);
          }

          try {
            Object result = proceedingJoinPoint.proceed();
            TypeAndString resultTypeAndString = SerializationUtils.convertToTypeAndString(result);
            this.communicationLogService.updateCommunicationAfterExecution(
                communicationLog, resultTypeAndString.getType(), resultTypeAndString.getString());
            return result;

          } catch (Throwable throwable) {
            communicationLogService.registerException(communicationLog, throwable);
            throw new RuntimeException(throwable);
          }
        });
  }

  private HostPluginFunctionCallCommunicationLog createCommunicationLog(
      ProceedingJoinPoint proceedingJoinPoint,
      CommunicationContext context,
      HostProvider hostProvider)
      throws DomainException {
    HostPluginFunctionCallCommunicationLog communicationLog;
    CommunicationLog parentCommunicationLog =
        context.getParentContext() != null
            ? context.getParentContext().getCurrentCommunicationLog()
            : null;
    HostPlugin hostPlugin = hostProvider.getPlugin();
    String hostPluginId = hostPlugin.getWrapper().getPluginId();
    String className = proceedingJoinPoint.getSignature().getDeclaringTypeName();
    String method = proceedingJoinPoint.getSignature().getName();
    List<Object> arguments = Arrays.asList(proceedingJoinPoint.getArgs());
    communicationLog =
        this.communicationLogService.logCommunicationBeforeExecution(
            ZonedDateTime.now(),
            parentCommunicationLog,
            hostPluginId,
            className,
            method,
            arguments);
    return communicationLog;
  }

}
