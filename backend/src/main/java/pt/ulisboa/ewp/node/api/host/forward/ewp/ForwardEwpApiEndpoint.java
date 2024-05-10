package pt.ulisboa.ewp.node.api.host.forward.ewp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ForwardEwpApiEndpoint {

  String api() default "";

  int apiMajorVersion() default -1;

  String endpoint() default "";

  /**
   * The parameter name of the request from which is to be extracted the target HEI ID.
   *
   * @return The target HEI ID of the request.
   */
  String targetHeiIdParameterName() default "";
}
