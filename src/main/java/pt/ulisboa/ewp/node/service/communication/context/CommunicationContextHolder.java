package pt.ulisboa.ewp.node.service.communication.context;

import java.util.function.Function;
import org.springframework.util.Assert;

public class CommunicationContextHolder {

  private static final ThreadLocal<CommunicationContext> contextHolder = new ThreadLocal<>();

  CommunicationContextHolder() {}

  public static void clearContext() {
    contextHolder.remove();
  }

  public static <T> T runInNestedContext(Function<CommunicationContext, T> callable) throws Exception {
    CommunicationContext originalContext = getContext();

    CommunicationContext nestedContext = new CommunicationContext(originalContext, null);
    contextHolder.set(nestedContext);

    try {
      return callable.apply(contextHolder.get());

    } finally {
      contextHolder.set(originalContext);
    }
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
