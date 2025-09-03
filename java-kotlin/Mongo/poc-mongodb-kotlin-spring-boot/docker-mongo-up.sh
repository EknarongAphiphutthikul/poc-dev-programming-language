#!/bin/bash

# Create directories if they don't exist
mkdir -p ./mongodb/data
mkdir -p ./mongodb/config
mkdir -p ./mongodb/logs

# Start MongoDB with Docker Compose
echo "Starting MongoDB container..."
docker-compose up -d

# Wait for MongoDB to be ready
echo "Waiting for MongoDB to be ready..."
sleep 10

# Check if MongoDB is running
if docker ps | grep -q "poc-mongodb"; then
    echo "MongoDB is running successfully on port 27030"
    echo "Connection string: mongodb://admin:password@localhost:27030/poc_db?authSource=admin"
else
    echo "Failed to start MongoDB"
    exit 1
fi