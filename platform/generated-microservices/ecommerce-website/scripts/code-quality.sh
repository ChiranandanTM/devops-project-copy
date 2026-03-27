#!/bin/bash
set -e

echo "Running JaCoCo code quality analysis for ecommerce-website..."
mvn clean test jacoco:report jacoco:check

# Extract coverage metrics
if [ -f "target/site/jacoco/index.html" ]; then
    echo "✓ JaCoCo report generated: target/site/jacoco/index.html"
    echo "✓ Coverage threshold: 80% enforced"
else
    echo "⚠ JaCoCo report not found"
fi
