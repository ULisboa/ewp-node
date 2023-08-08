#!/bin/bash

# Initialize local GIT repo
# This is necessary for EWP registry to run. However, it does not need to point to an actual remote GIT repo.
git init repo

# Import localhost certificate to CACERTS
CACERTS_STORE="/opt/java/openjdk/lib/security/cacerts"
KEYSTORE_PATH=/opt/keystore.p12
keytool -v -importkeystore -srckeystore ${KEYSTORE_PATH} -srcstoretype PKCS12 -srcstorepass p@ssw0rd -destkeystore $CACERTS_STORE -deststoretype JKS -deststorepass changeit -noprompt

exec /start-wrapper.sh