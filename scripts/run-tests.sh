#!/bin/bash
set -e

mvn clean test -f end-to-end-tests/pom.xml
