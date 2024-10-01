#!/bin/bash
DOCKER_COMPOSE_EXTRA_FLAGS=""

DOCKER_COMPOSE_DEV_OVERRIDE_FILE="docker-compose.dev.override.yml"
if test -f "$DOCKER_COMPOSE_DEV_OVERRIDE_FILE"; then
    echo "Found $DOCKER_COMPOSE_DEV_OVERRIDE_FILE, using it..."
    DOCKER_COMPOSE_EXTRA_FLAGS="$DOCKER_COMPOSE_EXTRA_FLAGS -f $DOCKER_COMPOSE_DEV_OVERRIDE_FILE"
fi

DOCKER_COMPOSE_ENV_DEV_FILE=".env.dev"
if test -f "$DOCKER_COMPOSE_ENV_DEV_FILE"; then
    echo "Found $DOCKER_COMPOSE_ENV_DEV_FILE, using it..."
    DOCKER_COMPOSE_EXTRA_FLAGS="$DOCKER_COMPOSE_EXTRA_FLAGS --env-file $DOCKER_COMPOSE_ENV_DEV_FILE"
fi

docker compose -f docker-compose.dev.yml ${DOCKER_COMPOSE_EXTRA_FLAGS} up --build