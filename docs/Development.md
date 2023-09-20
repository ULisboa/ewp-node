# Running EWP Node with Local EWP Registry

Sometimes it is useful to have a local EWP registry in order to test the correct behavior 
of host plugins.

## Preparation

These steps must be run inside the root folder of the EWP Node project.

1. Edit the file docker/registry/dev/data/manifest-sources.xml to use a correct HEI regular expression that 
matches the target HEI ID;

2. Copy the file backend/src/main/resources/application-dev.yml.example to backend/src/main/resources/application-dev.yml;

3. Edit the file backend/src/main/resources/application-dev.yml (only the sections of it mentioning that can be edited);

4. Authenticate in the Github Docker Registry (refer to https://github.com/erasmus-without-paper/ewp-registry-service#pull-the-image);

5. Copy the file .env.dev.example to .env.dev;

6. If necessary, change the values of .env.dev (e.g. if the default ports are already used).

7. Copy the file docker-compose.dev.override.yml.example to docker-compose.dev.override.yml;

8. If necessary, override Docker Compose services specification using docker-compose.dev.override.yml;

9. To install the certificate that the local EWP Node and EWP Registry use, execute:

    ```
    ./install-node-certificate.sh
    ```

10. If the EWP Node and/or EWP Registry is/are needed to be acessed outside the Docker environment (e.g. a web application not in the same Docker network that needs to communicate with the EWP Node), add the following lines to the file /etc/hosts:
    ```
    127.0.0.1   ewp-node
    127.0.0.1   ewp-registry
    ```

## Start local EWP Node and EWP Registry

Inside the root folder of the EWP Node project, follow the steps:

1. Execute:

    ```
    ./up_dev.sh
    ```

Once both Docker containers have started, unless the file .env.dev uses different ports, the EWP Node is available, on host, on port 8443, and the EWP Registry on port 8000. If .env configure different ports then consider those correct ports on this section, instead of the default ones.

If the step 10 of Preparation was executed, opening a web browser at https://ewp-node:8443/admin should show the Admin Dashboard authentication page, and opening at https://ewp-registry:8000/status should show a page with the status of the imported manifests (including the one of ewp-node).

## Stop local EWP Node and EWP Registry

Inside the root folder of the EWP Node project, follow the steps:

1. Execute:

    ```
    ./down_dev.sh
    ```

## Notes

- Initially, the EWP Registry may not be able to obtain the EWP Node manifest, as it is launching. However, the EWP Registry periodically will attempt to connect to the EWP Node manifest. Alternatively, on a web browser the page https://localhost:8000/status?url=https://ewp-node:8443/api/ewp/manifest (the port 8000 may need to be changed if the environment variable uses some other port for the EWP Registry) and force a reload;
- If there is a change of a host plugin, the containers must be restarted.


# Add support for new EWP API

## As Provider

1. Update the project https://github.com/ULisboa/ewp-host-plugin-skeleton:
    1. In package pt.ulisboa.ewp.host.plugin.skeleton.provider create a new class that extends
       HostVersionedProvider and contains abstract methods in order for EWP Node to obtain data from
       some source, in order to return to the requester.
        - As example, InstitutionsV2HostProvider may be used as reference.

2. In package pt.ulisboa.ewp.node.api.ewp.controller create a new controller that implement the
   endpoints that other EWP nodes will communicate to.
    - This controller must obtain the applicable host providers by looking up on the host plugin
      manager (HostPluginManager)
      and calling the interface methods defined on the previous point;
    - As example, EwpApiInstitutionsV2Controller may be used as reference.

## As Consumer

1. In package pt.ulisboa.ewp.node.client.ewp create a new client for the new EWP API that extends
   EwpApiClient<*T*> where *T* is a class that extends EwpApiConfiguration that provides details for
   that API (i.e. manifest entry details, such as URL(s), maximum number of parameters allowed,
   etc.);
    - As example, EwpInstitutionsV2Client may be used as reference.

2. In package pt.ulisboa.ewp.node.api.host.forward.ewp.controller create a new controller that
   extends AbstractForwardEwpApiController.
    - This controller must use the client created on the previous step as communication to other EWP
      nodes;
    - As example, ForwardEwpApiInstitutionsV2Controller may be used as reference.

3. Update the project https://github.com/ULisboa/forward-ewp-api-client:
    1. In package pt.ulisboa.forward.ewp.api.client.contract create a new class that extends BaseApi
       and provides interface methods to communicate with the new endpoints of the EWP Node for that
       new EWP API.
        - As example, InstitutionsV2Api may be used as reference.
    