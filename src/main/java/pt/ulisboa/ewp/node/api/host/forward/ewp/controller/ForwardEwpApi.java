package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ForwardEwpApi {

  /**
   * API local name, if applicable.
   */
  String apiLocalName() default "";

}
