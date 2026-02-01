#!/bin/bash
set -e

docker compose down -v #precaution

docker compose up -d
sleep 10
mvn clean test -f end-to-end-tests/pom.xml