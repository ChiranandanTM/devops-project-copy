#!/bin/bash
set -e

echo "Building inventory-service..."
mvn clean compile
echo "✓ Build completed successfully"
