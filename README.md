[![ULisboa](https://www.ulisboa.pt/sites/ulisboa.pt/themes/bs3/logo.png)](https://www.ulisboa.pt/)

# ewp-node
This repository contains code regarding a generic EWP implementation.

## Build

Run ```mvn clean package```.
If successful, a .jar file will be located at ewp-node/target/ that may be used to run the server.

## Build Docker image

Run ```docker build -t ewp-node .```

## Package Helm chart

Run ```helm package ./charts/ewp-node```

## Configuration

The application loads the application.yml file corresponding to the profile passed as VM options when launching the application.
For instance, if the application is launcher with the VM option ```-Dspring.profiles.active=dev``` then the 
configuration file ```application-dev.yml``` from the resources folder (src/main/resources) will be loaded.

The configuration file configures the database (for instance, for keystore management and requests logging), 
the EWP registry to use, basic data of the EWP node itself.

## Launch development version

To launch the application run the class EwpNodeApplication, following the guidelines of the Configuration section.

## Admin REST API documentation

The admin REST API documentation can be seen at http://localhost:8080/swagger-ui.

## Install/Upgrade release on Kubernetes using Helm

Run:
```
helm upgrade --install ${TGZ_FILE_PATH} --wait \
    --set namespace=${NAMESPACE},image.repository=${REGISTRY_URL}/${IMAGE_NAME},image.tag=${TAG},ingress.host=${HOST}
```

Change the variables accordingly.

For a full list of possible values to set consult charts/ewp-node/values.yaml.