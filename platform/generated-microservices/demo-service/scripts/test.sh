#!/bin/bash
set -e

echo "Running unit tests for demo-service..."
mvn test -DuseSystemClassLoader=false
echo "✓ Tests completed successfully"
