#!/bin/bash
set -e

echo "Running unit tests for inventory-service..."
mvn test -DuseSystemClassLoader=false
echo "✓ Tests completed successfully"
