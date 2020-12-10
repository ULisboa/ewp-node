package pt.ulisboa.ewp.node.api.ewp.controller;

import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.httpsig.v1.CliauthHttpsigV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.tlscert.v1.CliauthTlscertV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.srvauth.httpsig.v1.SrvauthHttpsigV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.srvauth.tlscert.v1.SrvauthTlscertV1;
import eu.erasmuswithoutpaper.api.specs.sec.intro.HttpSecurityOptions;

public abstract class EwpApiManifestEntryWithHttpSecurityOptionsStrategy
    extends EwpApiManifestEntryStrategy {

  protected HttpSecurityOptions getHttpSecurityOptions() {
    HttpSecurityOptions httpSecurityOptions = new HttpSecurityOptions();

    HttpSecurityOptions.ClientAuthMethods clientAuthMethods =
        new HttpSecurityOptions.ClientAuthMethods();

    CliauthTlscertV1 cliauthTlscert = new CliauthTlscertV1();
    cliauthTlscert.setAllowsSelfSigned(true);
    clientAuthMethods.getAny().add(cliauthTlscert);

    clientAuthMethods.getAny().add(new CliauthHttpsigV1());

    httpSecurityOptions.setClientAuthMethods(clientAuthMethods);

    HttpSecurityOptions.ServerAuthMethods serverAuthMethods =
        new HttpSecurityOptions.ServerAuthMethods();

    serverAuthMethods.getAny().add(new SrvauthTlscertV1());
    serverAuthMethods.getAny().add(new SrvauthHttpsigV1());

    httpSecurityOptions.setServerAuthMethods(serverAuthMethods);

    return httpSecurityOptions;
  }
}
