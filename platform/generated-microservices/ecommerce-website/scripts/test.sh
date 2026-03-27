#!/bin/bash
set -e

echo "Running unit tests for ecommerce-website..."
mvn test -DuseSystemClassLoader=false
echo "✓ Tests completed successfully"
