#!/bin/bash

# Remove MySQL data, and config directories using docker-compose
docker compose down --volumes

rm -rf mysql