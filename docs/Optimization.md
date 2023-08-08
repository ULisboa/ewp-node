# Database

## Request logs

By default, the EWP Node logs requests from/to other EWP Nodes, as well Forward EWP requests.
Given, that the request/response's body is saved, the storage is non-negligible.
For that, it is recommended that the database is configured to compress data.

For instance, MariaDB supports several methodologies of
compression (https://mariadb.com/kb/en/optimization-and-tuning-compression/).