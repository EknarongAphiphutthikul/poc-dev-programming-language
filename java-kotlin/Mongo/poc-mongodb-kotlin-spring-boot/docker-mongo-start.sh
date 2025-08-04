#!/bin/bash

echo "Starting existing MongoDB container..."
docker-compose start mongodb

# Wait for MongoDB to be ready
echo "Waiting for MongoDB to be ready..."
sleep 5

# Check if MongoDB is running
if docker ps | grep -q "poc-mongodb"; then
    echo "MongoDB container started successfully"
    echo "Connection string: mongodb://admin:password@localhost:27030/poc_db?authSource=admin"
else
    echo "Failed to start MongoDB container"
    exit 1
fi