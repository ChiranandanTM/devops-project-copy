#!/bin/bash
set -e

echo "Building Docker image for ecommerce-website..."

IMAGE_TAG="com-ecommerce/ecommerce-website:${GIT_COMMIT_SHORT}"
IMAGE_LATEST="com-ecommerce/ecommerce-website:latest"

docker build -t "${IMAGE_TAG}" -t "${IMAGE_LATEST}" .

echo "✓ Docker image built successfully"
echo "  - ${IMAGE_TAG}"
echo "  - ${IMAGE_LATEST}"

# Optional: Push to registry
# docker push "${IMAGE_TAG}"
# docker push "${IMAGE_LATEST}"
