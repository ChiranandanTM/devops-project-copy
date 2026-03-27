#!/bin/bash
set -e

echo "Building ecommerce-website..."
mvn clean compile
echo "✓ Build completed successfully"
