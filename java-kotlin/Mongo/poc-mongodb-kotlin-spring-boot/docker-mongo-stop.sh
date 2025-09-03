#!/bin/bash

echo "Stopping MongoDB container..."
docker-compose stop

# Check if container is stopped
if ! docker ps | grep -q "poc-mongodb"; then
    echo "MongoDB container stopped successfully"
else
    echo "Failed to stop MongoDB container"
    exit 1
fi