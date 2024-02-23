#!/bin/bash
docker-compose -f docker-compose.dev.registry.yml --env-file .env.dev up -d --build