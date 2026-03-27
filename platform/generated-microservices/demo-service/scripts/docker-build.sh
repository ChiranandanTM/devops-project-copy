#!/bin/bash
set -e

echo "Building Docker image for demo-service..."

IMAGE_TAG="com-demo/demo-service:${GIT_COMMIT_SHORT}"
IMAGE_LATEST="com-demo/demo-service:latest"

docker build -t "${IMAGE_TAG}" -t "${IMAGE_LATEST}" .

echo "✓ Docker image built successfully"
echo "  - ${IMAGE_TAG}"
echo "  - ${IMAGE_LATEST}"

# Optional: Push to registry
# docker push "${IMAGE_TAG}"
# docker push "${IMAGE_LATEST}"
