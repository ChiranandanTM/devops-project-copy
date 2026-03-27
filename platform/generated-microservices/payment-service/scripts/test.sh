#!/bin/bash
set -e

echo "Running unit tests for payment-service..."
mvn test -DuseSystemClassLoader=false
echo "✓ Tests completed successfully"
