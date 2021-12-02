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
| :----: | :---: |:------------------------:|
| Discovery Manifest | :heavy_check_mark: |            5             |
| Echo | :heavy_check_mark: |            2             |
| Institutions | :heavy_check_mark: |            2             |
| Organizational Units | :heavy_check_mark: |            2             |
| Courses | :heavy_check_mark: |            0             |
| Simple Course Replication | :heavy_check_mark: |            1             |
| Interinstitutional Agreements | :heavy_check_mark: |            4             |
| Interinstitutional Agreements CNR | :heavy_check_mark: |            2             |
| Interinstitutional Approval | :heavy_check_mark: |            1             |
| Interinstitutional Approval CNR | :heavy_check_mark: |            1             |
| Mobility Factsheet | :heavy_check_mark: |            1             |
| Outgoing Mobilities | :heavy_check_mark: |            1             |
| Outgoing Mobility Learning Agreements | :heavy_check_mark: |            1             |
| Outgoing Mobility CNR | :heavy_check_mark: |            1             |
| Outgoing Mobility Learning Agreement CNR | :heavy_check_mark: |            1             |
| Incoming Mobilities |  |                          |
| Incoming Mobility CNR |  |                          |
| Incoming Mobility ToR |  |                          |
| Incoming Mobility ToR CNR |  |                          |

### As Consumer

| API | Supported? | Major Versions Supported |
| :----: | :---: | :---: |
| Institutions | :heavy_check_mark: | 2 |
| Organizational Units | :heavy_check_mark: | 2 |
| Courses | :heavy_check_mark: | 0 |
| Simple Course Replication | :heavy_check_mark: | 1 |
| Interinstitutional Agreements | :heavy_check_mark: | 3 & 4 & 6 |
| Interinstitutional Agreements CNR | :heavy_check_mark: | 2 |
| Interinstitutional Approval | :heavy_check_mark: | 1 |
| Interinstitutional Approval CNR | :heavy_check_mark: | 1 |
| Mobility Factsheet | :heavy_check_mark: | 1 |
| Outgoing Mobilities | :heavy_check_mark: | 1 |
| Outgoing Mobility Learning Agreements | :heavy_check_mark: | 1 |
| Outgoing Mobility CNR | :heavy_check_mark: | 1 |
| Outgoing Mobility Learning Agreement CNR | :heavy_check_mark: | 1 |
| Incoming Mobilities | :heavy_check_mark: | 1 |
| Incoming Mobility CNR | :heavy_check_mark: | 1 |
| Incoming Mobility ToR | :heavy_check_mark: | 1 |
| Incoming Mobility ToR CNR | :heavy_check_mark: | 1 |

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