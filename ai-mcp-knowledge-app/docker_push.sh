#/bin/bash

# https://cr.console.aliyun.com/cn-hangzhou/instance/credentials

# Ensure the script exits if any command fails
set -e

# Define variables for the registry and image
ALIYUN_REGISTRY="registry.cn-hangzhou.aliyuncs.com"
NAMESPACE="xpeanut_stdio"
IMAGE_NAME="ai-mcp-knowledge-app"
IMAGE_TAG="1.0"

# Login to Aliyun Docker Registry
echo "Logging into Aliyun Docker Registry..."
sudo docker login --username="15732156810" --password="1qazccde3!@#" $ALIYUN_REGISTRY

# Tag the Docker image
echo "Tagging the Docker image..."
docker tag ${NAMESPACE}/${IMAGE_NAME}:${IMAGE_TAG} ${ALIYUN_REGISTRY}/${NAMESPACE}/${IMAGE_NAME}:${IMAGE_TAG}

# Push the Docker image to Aliyun
echo "Pushing the Docker image to Aliyun..."
docker push ${ALIYUN_REGISTRY}/${NAMESPACE}/${IMAGE_NAME}:${IMAGE_TAG}

echo "Docker image pushed successfully! "

echo "检出地址：docker pull ${ALIYUN_REGISTRY}/${NAMESPACE}/${IMAGE_NAME}:${IMAGE_TAG}"
echo "标签设置：docker tag ${ALIYUN_REGISTRY}/${NAMESPACE}/${IMAGE_NAME}:${IMAGE_TAG} ${NAMESPACE}/${IMAGE_NAME}:${IMAGE_TAG}"

# Logout from Aliyun Docker Registry
echo "Logging out from Aliyun Docker Registry..."
docker logout $ALIYUN_REGISTRY
