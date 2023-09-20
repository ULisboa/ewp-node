#!/bin/bash
DOCKER_COMPOSE_DEV_OVERRIDE_FILE="docker-compose.dev.override.yml"
DOCKER_COMPOSE_EXTRA_FLAGS=""
if test -f "$DOCKER_COMPOSE_DEV_OVERRIDE_FILE"; then
    echo "Found $DOCKER_COMPOSE_DEV_OVERRIDE_FILE, using it..."
    DOCKER_COMPOSE_EXTRA_FLAGS="$DOCKER_COMPOSE_EXTRA_FLAGS -f $DOCKER_COMPOSE_DEV_OVERRIDE_FILE"
fi
docker-compose -f docker-compose.dev.yml ${DOCKER_COMPOSE_EXTRA_FLAGS} --env-file .env.dev up -d --build