<h1 align="center">
  <br>
  <a href="https://www.erasmuswithoutpaper.eu/"><img src="https://developers.erasmuswithoutpaper.eu/logo.png" alt="EWP" width="350"></a>
  <a href="https://www.ulisboa.pt/"><img src="https://rem.rc.iseg.ulisboa.pt/img/logo_ulisboa.png" alt="ULisboa" width="380"></a>
    <br>
  EWP Node
  <br>
</h1>

<h4 align="center">A generic and flexible EWP node implementation.</h4>

## Work in Progress warning

This project is still early in development. Therefore, until the first major version is released,
non-backward changes may be introduced.

## APIs Coverage

### As Provider

| API | Supported? | Major Versions Supported |
| :----: | :---: | :---: |
| Discovery Manifest | X | 5 |
| Echo | X | 2 |
| Institutions | X | 2 |
| Organizational Units | X | 2 |
| Courses | X | 0 |
| Simple Course Replication | X | 1 |
| Interinstitutional Agreements | X | 4 |
| Interinstitutional Agreements CNR |  |  |
| Interinstitutional Approval | X | 1 |
| Interinstitutional Approval CNR |  |  |
| Mobility Factsheet | X | 1 |
| Outgoing Mobilities |  |  |
| Outgoing Mobility Learning Agreements |  |  |
| Outgoing Mobility CNR |  |  |
| Outgoing Mobility Learning Agreement CNR |  |  |
| Incoming Mobilities |  |  |
| Incoming Mobility CNR |  |  |
| Incoming Mobility ToR |  |  |
| Incoming Mobility ToR CNR |  |  |

### As Consumer

| API | Supported? | Major Versions Supported |
| :----: | :---: | :---: |
| Institutions | X | 2 |
| Organizational Units | X | 2 |
| Courses | X | 0 |
| Simple Course Replication | X | 1 |
| Interinstitutional Agreements | X | 3 & 4 |
| Interinstitutional Agreements CNR |  |  |
| Interinstitutional Approval | X | 1 |
| Interinstitutional Approval CNR |  |  |
| Mobility Factsheet | X | 1 |
| Outgoing Mobilities | X | 1 |
| Outgoing Mobility Learning Agreements |  |  |
| Outgoing Mobility CNR |  |  |
| Outgoing Mobility Learning Agreement CNR |  |  |
| Incoming Mobilities | X | 1 |
| Incoming Mobility CNR |  |  |
| Incoming Mobility ToR |  |  |
| Incoming Mobility ToR CNR |  |  |

## Requirements

To clone and run this project, you'll need [Git](https://git-scm.com) and, depending on your
preference,
[Maven](https://maven.apache.org/) or [Docker](https://www.docker.com/).

## Cloning the Project

To clone the project run:
```
git clone --recursive https://github.com/ULisboa/ewp-node
```
Note the ```--recursive``` flag that is needed so the external dependencies configured as submodules are also cloned.

## Building and Running with Maven

Run the command line:
```
mvn spring-boot:run
```

## Building and Running with Docker

### Building the Docker image

Run the command line:
```
docker build -t ulisboa/ewp-node .
```

### Running the Docker image

#### Directly with Docker

Run the command line (check the section [Docker Image Parameters](#docker-image-parameters) for reference):
```
docker run \
    --name=ewp-node \
    -p 8080:8080 \
    -v <path to config folder>:/config \
    -v <path to logs folder>:/logs \
    --restart unless-stopped \
    ulisboa/ewp-node
```

#### With Docker Compose

Write into a docker-compose.yml file (check the section [Docker Image Parameters](#docker-image-parameters) for reference):
```
---
version: "2.1"
services:
  ewp-node:
    image: ulisboa/ewp-node
    container_name: ewp-node
    volumes:
      - <path to config folder>:/config
      - <path to logs folder>:/logs
    ports:
      - 8080:8080
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "--no-check-certificate", "http://localhost:8080/rest/healthcheck"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s
    restart: unless-stopped
```

Then run on the folder containing that file:
```
docker-compose up -d
```

Note: This configuration can be adapted to run on Docker Swarm.

### Docker Image Parameters

Container images are configured using parameters passed at runtime.
These parameters are separated by a colon and indicate ```<external>:<internal>``` respectively.
For example, ```-p 80:8080``` would expose port 8080 from inside the container to be accessible
from the host's IP on port 80 outside the container.


| Parameter | Function |
| :----: | --- |
| `-p 8080` | Port used by the server |
| `-v /config` | Path from where the server will read the configuration when starting. Namely, it expects a file application.yml with the same structure as [src/main/resources/application.yml](src/main/resources/application.yml) (check this file for an example as well documentation on it). |
| `-v /logs` | Path where the server will store the logs. |

## Automatic APIs documentation

When the project is running, the endpoint [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) 
will provide an interface to the APIs automatic documentation.

## License

This project is licensed under the terms of the [MIT license](LICENSE).