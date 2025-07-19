#!/bin/bash

# Make sure folders exist
mkdir -p mysql/conf.d mysql/data

# Start the container
docker compose up -d