# 0.11.0

Previously, the Forward EWP APIs used as issuer (set on ClientConfiguration of
forward-ewp-api-client) for the JWT token the code of the host.
Starting on this version, the issuer is the ID of a client configured on the application properties
file.

For that, where the application properties file had a fragment such as:

```yaml
bootstrap:
  hosts:
    - code: <code of the host>
      ...
      forwardEwpApi:
        secret: <original secret>
```

Now, that fragment must be configured as:

```yaml
bootstrap:
  hosts:
    - code: <code of the host>
      ...
      forwardEwpApi:
        clients:
          - id: <unique ID of the client>
            secret: <original secret>
```

The client ID must be unique among all clients, independent of the host where it is specified.
It is possible to configure multiple clients for the same host.