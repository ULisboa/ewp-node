package pt.ulisboa.ewp.node.service.communication.context;

import org.springframework.util.Assert;

public class CommunicationContextHolder {

  private static final ThreadLocal<CommunicationContext> contextHolder = new ThreadLocal<>();

  CommunicationContextHolder() {}

  public static void clearContext() {
    contextHolder.remove();
  }

  public static CommunicationContext getContext() {
    CommunicationContext context = contextHolder.get();
    if (context == null) {
      context = createEmptyContext();
      contextHolder.set(context);
    }
    return context;
  }

  public static void setContext(CommunicationContext context) {
    Assert.notNull(context, "Only non-null CommunicationContext instances are permitted");
    contextHolder.set(context);
  }

  private static CommunicationContext createEmptyContext() {
    return new CommunicationContext();
  }
}
