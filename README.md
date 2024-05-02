<h1 align="center">
  <br>
  <a href="https://www.erasmuswithoutpaper.eu/"><img src="https://developers.erasmuswithoutpaper.eu/logo.png" alt="EWP" width="350"></a>
  <a href="https://www.ulisboa.pt/"><img src="https://rem.rc.iseg.ulisboa.pt/img/logo_ulisboa.png" alt="ULisboa" width="380"></a>
    <br>
  EWP Node
  <br>
</h1>

<p align="center">
  <a href="https://github.com/ULisboa/ewp-node/actions/workflows/docker-image.yml">
    <img src="https://github.com/ulisboa/ewp-node/actions/workflows/docker-image.yml/badge.svg?branch=master"
        alt="Docker Image CI">
  </a>
  <a href="https://github.com/ULisboa/ewp-node/blob/master/LICENSE">
    <img alt="GitHub" src="https://img.shields.io/github/license/ulisboa/ewp-node">
  </a>
  <img src="https://img.shields.io/badge/JDK-11+-green.svg" alt="JDK 11+">
</p>

<h4 align="center">A generic and flexible EWP node implementation.</h4>

## Work in Progress warning

This project is still early in development. Therefore, until the first major version is released,
non-backward changes may be introduced.

## APIs Coverage

### As Provider

|                   API                    | Supported? | Major Versions Supported |
|:----------------------------------------:| :---: |:------------------------:|
|            Discovery Manifest            | :heavy_check_mark: |            6             |
|                   Echo                   | :heavy_check_mark: |            2             |
|               Institutions               | :heavy_check_mark: |            2             |
|           Organizational Units           | :heavy_check_mark: |            2             |
|                 Courses                  | :heavy_check_mark: |            0             |
|        Simple Course Replication         | :heavy_check_mark: |            1             |
|                  Files                   | :heavy_check_mark: |            1             |
|      Interinstitutional Agreements       | :heavy_check_mark: |            7             |
|    Interinstitutional Agreements CNR     | :heavy_check_mark: |            3             |
|       Interinstitutional Approval        | :heavy_check_mark: |            2             |
|     Interinstitutional Approval CNR      | :heavy_check_mark: |            2             |
|            Mobility Factsheet            | :heavy_check_mark: |            1             |
|           Outgoing Mobilities            | :heavy_check_mark: |            2             |
|  Outgoing Mobility Learning Agreements   | :heavy_check_mark: |            1             |
|          Outgoing Mobility CNR           | :heavy_check_mark: |            1             |
| Outgoing Mobility Learning Agreement CNR | :heavy_check_mark: |            1             |
|           Incoming Mobilities            | :heavy_check_mark: |            1             |
|          Incoming Mobility CNR           | :heavy_check_mark: |            1             |
|          Incoming Mobility ToR           | :heavy_check_mark: |          1 & 2           |
|        Incoming Mobility ToR CNR         | :heavy_check_mark: |            1             |

### As Consumer

|                   API                    |     Supported?     | Major Versions Supported |
|:----------------------------------------:|:------------------:|:------------------------:|
|               Institutions               | :heavy_check_mark: |            2             |
|           Organizational Units           | :heavy_check_mark: |            2             |
|                 Courses                  | :heavy_check_mark: |            0             |
|        Simple Course Replication         | :heavy_check_mark: |            1             |
|                  Files                   | :heavy_check_mark: |            1             |
|      Interinstitutional Agreements       | :heavy_check_mark: |            7             |
|    Interinstitutional Agreements CNR     | :heavy_check_mark: |            3             |
|       Interinstitutional Approval        | :heavy_check_mark: |            2             |
|     Interinstitutional Approval CNR      | :heavy_check_mark: |            2             |
|            Mobility Factsheet            | :heavy_check_mark: |            1             |
|           Outgoing Mobilities            | :heavy_check_mark: |            2             |
|  Outgoing Mobility Learning Agreements   | :heavy_check_mark: |            1             |
|          Outgoing Mobility CNR           | :heavy_check_mark: |            1             |
| Outgoing Mobility Learning Agreement CNR | :heavy_check_mark: |            1             |
|           Incoming Mobilities            | :heavy_check_mark: |            1             |
|          Incoming Mobility CNR           | :heavy_check_mark: |            1             |
|          Incoming Mobility ToR           | :heavy_check_mark: |          1 & 2           |
|        Incoming Mobility ToR CNR         | :heavy_check_mark: |            1             |

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

## Building and Running with an IDE

1. Import the project backend on an IDE (e.g. Intellij IDEA);

2. Execute the class pt.ulisboa.ewp.node.EwpNodeApplication

  - Recommended: To use a development profile, pass a VM argument: ```-Dspring.profiles.active=dev```

## Building and Running with Docker

### Development Environment

Refer to the [Development documentation](docs/Development.md).

### Production Environment

#### Building the Docker image

Run the command line:
```
docker build -t ulisboa/ewp-node .
```

#### Running the Docker image (with Docker Compose)

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
      - <path to logs folder>:/logs # Optional
      - <path to plugins folder>:/plugins # Optional
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

Notes: 
- If SSL is enabled then every reference in the docker-compose.yml file to port 8080 must be changed accordingly, including changing "http" to "https" on the healthcheck test command;
- This configuration can be adapted to run on Docker Swarm.

### Docker Image Parameters

Container images are configured using parameters passed at runtime.
These parameters are separated by a colon and indicate ```<external>:<internal>``` respectively.
For example, ```-p 80:8080``` would expose port 8080 from inside the container to be accessible
from the host's IP on port 80 outside the container.


| Parameter | Function |
| :----: | --- |
| `-p 8080` | Port used by the server |
| `-p 8443` | Port used by the server (if SSL is enabled) |
| `-v /config` | Path from where the server will read the configuration when starting. Namely, it expects a file application.yml with the same structure as [src/main/resources/application.yml](src/main/resources/application.yml) (check this file for an example as well documentation on it). |
| `-v /logs` | Path where the server will store the logs. |
| `-v /plugins` | Path where the server will store the plugins. |

## Automatic APIs documentation

When the project is running, the endpoint [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) 
will provide an interface to the APIs automatic documentation.

## Additional documentation

More documentation is available on the folder [docs/](docs/).

## License

This project is licensed under the terms of the [MIT license](LICENSE).