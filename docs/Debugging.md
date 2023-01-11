# Communication Diagrams

Whenever a host communicates with the node or the node communicates with some node of the EWP
network, the HTTP communication is logged.

On communications from a host, the produced responses contain the ID of the logged communication.
This ID may be provided to the actuator API in order to obtain a sequence diagram of the
communication.
This sequence diagram will contain the request from the host, subsequent requests that the node
makes to other nodes and, finally, the response returned to the host.

This API is available by accessing the following URI of the node, on a browser:

```/actuator/communications/diagrams/<id>```

By default, this feature is disabled.
However, it may be enabled by configuring the management and
actuator sections on the application properties file (see application-dev.yml.example for an example
on how
to configure it).
It is strongly recommended to configure the actuator section in order to protect the actuator API.