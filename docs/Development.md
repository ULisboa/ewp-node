# Running EWP Node with Local EWP Registry

Sometimes it is useful to have a local EWP registry in order to test the correct behavior 
of host plugins.

## Preparation

These steps must be run inside the root folder of the EWP Node project.

1. Edit the file docker/registry/dev/data/manifest-sources.xml to use a correct HEI regular expression that 
matches the target HEI ID;

2. Copy the file backend/src/main/resources/application-dev.yml.example to backend/src/main/resources/application-dev.yml;

3. Edit the file backend/src/main/resources/application-dev.yml (only the sections of it mentioning that can be edited);
   
4. Build local EWP Node Docker image:
    ```
    docker build -f Dockerfile.dev -t ewp-node:dev . 
    ```

## Running local EWP Node and EWP Registry

These steps must be run inside the root folder of the EWP Node project.

1. Launch the EWP Node by running (this considers that there is a local folder plugins/):
    ```
     docker run --rm --name ewp-node -v ${PWD}/backend/src/main/resources/application-dev.yml:/config/application.yml -v ${PWD}/plugins:/plugins --net=host ewp-node:dev 
    ```

2. Authenticate in the Github Docker Registry (refer to https://github.com/erasmus-without-paper/ewp-registry-service#pull-the-image);

3. Launch the EWP Registry by running:
    ```
    docker run --rm -it --name ewp-registry --net=host -v ${PWD}/docker/registry/dev/data:/root -v ${PWD}/backend/src/main/resources/keystore/localhost.p12:/opt/keystore.p12 --entrypoint /root/entrypoint.sh docker.pkg.github.com/erasmus-without-paper/ewp-registry-service/ewp-registry-service:latest
    ```

Once both Docker containers have started, the EWP Node is available on port 8443, and the EWP Registry on port 8000.

### Notes

- Debugging of EWP Node is available on port 5005;
- If there is a change of a host plugin, both the docker container of the EWP Node and EWP Registry must be restarted.
By default, the EWP Registry periodically reloads the manifest every 5 minutes, hence the need to restart it if the host plugin has changed.

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
    