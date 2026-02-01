#!/bin/bash
set -e

mvn clean package -pl '!end-to-end-tests'

docker build -t dtu-account-service -f account-service/Dockerfile .
docker build -t dtu-token-service -f token-service/Dockerfile .
docker build -t dtu-payment-service -f payment-service/Dockerfile .
docker build -t dtu-report-service -f report-service/Dockerfile .
docker build -t dtu-facade -f facade/Dockerfile .
