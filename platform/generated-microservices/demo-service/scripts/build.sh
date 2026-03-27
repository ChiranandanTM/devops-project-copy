#!/bin/bash
set -e

echo "Building demo-service..."
mvn clean compile
echo "✓ Build completed successfully"
