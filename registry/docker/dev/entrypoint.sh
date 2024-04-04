#!/bin/bash

if [[ -n "$EWP_NODE_HOSTNAME" ]]; then

    if [ -z "$EWP_NODE_PORT" ]; then
    echo "EWP_NODE_PORT environment variable is not set or is empty"
    fi

    if [ -z "$EWP_NODE_HEI_REGEX" ]; then
    echo "EWP_NODE_HEI_REGEX environment variable is not set or is empty"
    fi

    EWP_NODE_MANIFEST_URL="https://${EWP_NODE_HOSTNAME}:${EWP_NODE_PORT}/api/ewp/manifest"

    # Configure a single manifest source to point to the EWP Node service
    echo "<manifest-sources>\
        <source>\
            <location>${EWP_NODE_MANIFEST_URL}</location>\
            <hei-regex>${EWP_NODE_HEI_REGEX}</hei-regex>\
        </source>\
    </manifest-sources>" > /root/manifest-sources.xml

    if [[ -n "$WAIT_FOR_EWP_NODE" ]]; then
        SLEEP_DURATION=5
        echo "Waiting until '$EWP_NODE_MANIFEST_URL' is alive"
        while true; do
            curl -s --fail -k "$EWP_NODE_MANIFEST_URL" >/dev/null
            status=$?
            if [ $status -eq 0 ]; then
                echo "EWP Node is alive!"
                break
            else
                sleep $SLEEP_DURATION
            fi
        done
    fi
    
fi

java -XX:-OmitStackTraceInFastThrow ${JAVA_OPTS} -jar /app.jar