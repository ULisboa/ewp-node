package pt.ulisboa.ewp.node.api.ewp.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EwpApiEndpoint {

  String api() default "";

  int apiMajorVersion() default -1;

  String endpoint() default "";
}
