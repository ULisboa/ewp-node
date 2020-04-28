package pt.ulisboa.ewp.node.api.ewp.controller;

import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.httpsig.CliauthHttpsig;
import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.tlscert.CliauthTlscert;
import eu.erasmuswithoutpaper.api.client.auth.methods.srvauth.httpsig.SrvauthHttpsig;
import eu.erasmuswithoutpaper.api.client.auth.methods.srvauth.tlscert.SrvauthTlscert;
import eu.erasmuswithoutpaper.api.specs.sec.intro.HttpSecurityOptions;

public abstract class EwpApiManifestEntryWithHttpSecurityOptionsStrategy
    extends EwpApiManifestEntryStrategy {

  protected HttpSecurityOptions getHttpSecurityOptions() {
    HttpSecurityOptions httpSecurityOptions = new HttpSecurityOptions();

    HttpSecurityOptions.ClientAuthMethods clientAuthMethods =
        new HttpSecurityOptions.ClientAuthMethods();

    CliauthTlscert cliauthTlscert = new CliauthTlscert();
    cliauthTlscert.setAllowsSelfSigned(true);
    clientAuthMethods.getAny().add(cliauthTlscert);

    clientAuthMethods.getAny().add(new CliauthHttpsig());

    httpSecurityOptions.setClientAuthMethods(clientAuthMethods);

    HttpSecurityOptions.ServerAuthMethods serverAuthMethods =
        new HttpSecurityOptions.ServerAuthMethods();

    serverAuthMethods.getAny().add(new SrvauthTlscert());
    serverAuthMethods.getAny().add(new SrvauthHttpsig());

    httpSecurityOptions.setServerAuthMethods(serverAuthMethods);

    return httpSecurityOptions;
  }
}
