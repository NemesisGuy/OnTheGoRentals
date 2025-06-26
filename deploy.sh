#!/bin/bash

set -e  # Exit on any error

COMPONENT=$1
VERSION=$2

if [ -z "$COMPONENT" ] || [ -z "$VERSION" ]; then
  echo "Usage: $0 [frontend|backend] [version]"
  exit 1
fi

if [[ "$COMPONENT" != "frontend" && "$COMPONENT" != "backend" ]]; then
  echo "❌ Component must be 'frontend' or 'backend'"
  exit 1
fi

IMAGE="nemesisguy/on-the-go-rentals-$COMPONENT"
DOCKERFILE="docker/Dockerfile"

echo "🔧 Starting build for $COMPONENT:$VERSION..."

if [ "$COMPONENT" == "backend" ]; then
  echo "📦 Running Maven clean & package..."
  if [ -f "./mvnw" ]; then
    ./mvnw clean package -DskipTests
  else
    echo "⚠️ Maven wrapper not found. Using system Maven..."
    mvn clean package -DskipTests
  fi

  JAR_COUNT=$(ls target/*.jar 2>/dev/null | wc -l)
  if [ "$JAR_COUNT" -eq 0 ]; then
    echo "❌ No JAR found in target/. Check Maven build."
    exit 1
  fi
fi


# Proceed with Docker build
docker build --no-cache -f "$DOCKERFILE" -t "$IMAGE:$VERSION" -t "$IMAGE:latest" .

echo "🚀 Pushing Docker images to Docker Hub..."
docker push "$IMAGE:$VERSION"
docker push "$IMAGE:latest"

echo "✅ Deployment complete for $IMAGE:$VERSION"
