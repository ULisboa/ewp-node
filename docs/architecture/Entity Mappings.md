# Overview

Given for a specific HEI ID, there may be several host plugins associated. 

This means that some of those host plugins may cover some organizational units, while other host plugins
cover other organizational units.

Therefore, the EWP Node must be able to map entities that may be of a specific organizational unit
to the correct host plugin.
This is done automatically, upon certain requests to the Forward EWP APIs, or 
periodically, via syncing services.

# Interinstitutional Agreements

## CNR Forward EWP API

When the EWP Node receives a request to forward a CNR notification to a target institution,
the EWP Node uses the information of the request to store the mapping.

## Sync service

Periodically, the node makes a call to the Index of the InterInstitutional Agreements EWP API of each plugin host, 
passing as hei_id the HEI ID covered by the plugin host in question, thus obtaining all the IIA IDs known by the host. 
For each of these IDs, a call is made to the GET of the InterInstitutional Agreements EWP API, 
using the ID in question and, if the IIA in question has as one of the agreement partners the 
original HEI ID then a local mapping is created, if it is new, based on the information returned by the plugin host.

# Outgoing Mobility / Outgoing Mobility Leaning Agreements

## CNR Forward EWP API

When the EWP Node receives a request to forward a CNR notification to a target institution,
the EWP Node uses the information of the request to store the mapping.

## Forward EWP APIs

When the EWP Node makes a call to obtain some outgoing mobility (learning agreement), 
it uses the obtained information to store the respective mappings.

## Sync Service

Periodically, the node makes a call to the Index of the Outgoing Mobility EWP API 
of each plugin host, passing as sending_hei_id the HEI ID covered by the plugin 
host in question, thus obtaining all Outgoing Mobility IDs. For each of these IDs, 
a GET call is made from the Outgoing Mobility EWP API to the plugin host, using 
the ID in question, creating, if new, a local mapping based on the information 
returned by the plugin host.


