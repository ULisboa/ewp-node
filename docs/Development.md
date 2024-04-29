# Running EWP Node with Local EWP Registry

The following procedure includes the deployment of a local EWP Registry.
This allows complete local testing of the behavior of host plugins.

## Preparation

These steps must be run inside the root folder of the EWP Node project.

1. Copy the file backend/src/main/resources/application-dev.yml.example to backend/src/main/resources/application-dev.yml;

2. Edit the file backend/src/main/resources/application-dev.yml (only the sections of it mentioning that can be edited);

3. Copy the file .env.dev.example to .env.dev;

4. If necessary, change the values of .env.dev (e.g. if the default external ports are already used).

5. Copy the file docker-compose.dev.override.yml.example to docker-compose.dev.override.yml;

6. If necessary, override Docker Compose services specification using docker-compose.dev.override.yml;

7. To install the certificate that the local EWP Node and EWP Registry use, execute:

    ```
    ./install-node-certificate.sh
    ```

8. [Recommended, this step allows to access both ewp-node and ewp-registry outside the Docker environment] Add the following lines to the file /etc/hosts:
    ```
    127.0.0.1   ewp-node
    127.0.0.1   ewp-registry
    ```

## Start local EWP Node and EWP Registry

Inside the root folder of the EWP Node project, follow the steps:

1. Execute:

    ```
    sh up_dev.sh
    ```

Once both Docker containers have started, unless the file .env.dev uses different ports, the EWP Node is available, on host, on port 8443 (backend), port 4200 (frontend), and port 5005 (debugging on backend), and the EWP Registry on port 8000. If .env.dev configures different ports then consider those correct ports on this section, instead of the default ones.

If the step 8 of Preparation was executed, opening a web browser at https://ewp-node:4200/admin should show the Admin Dashboard authentication page, and opening at https://ewp-registry:8000/status should show a page with the status of the imported manifests (including the one of ewp-node).

Notes:
 - The file docker-compose.dev.override.yml and .env.dev may be edited freely as those are not commited to the repository.
 - If changes have been made to frontend/backend, just rerun the command. It will rebuild and recreate the Docker container.


## Stop local EWP Node and EWP Registry

Inside the root folder of the EWP Node project, follow the steps:

1. Execute:

    ```
    sh down_dev.sh
    ```

## Notes

- Initially, the EWP Registry may not be able to obtain the EWP Node manifest, as it is launching. However, the EWP Registry periodically will attempt to connect to the EWP Node manifest. Alternatively, on a web browser the page https://ewp-registry:8000/status?url=https://ewp-node:8443/api/ewp/manifest (the port 8000 may need to be changed if the environment variable uses some other port for the EWP Registry) and force a reload;
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
    