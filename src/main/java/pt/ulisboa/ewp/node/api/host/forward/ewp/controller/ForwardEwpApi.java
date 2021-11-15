package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import pt.ulisboa.ewp.node.utils.EwpApi;

@Retention(RetentionPolicy.RUNTIME)
public @interface ForwardEwpApi {

  EwpApi value();

}
