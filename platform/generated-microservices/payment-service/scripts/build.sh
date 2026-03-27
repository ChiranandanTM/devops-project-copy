#!/bin/bash
set -e

echo "Building payment-service..."
mvn clean compile
echo "✓ Build completed successfully"
