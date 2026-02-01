#!/bin/bash
set -e

./scripts/install-libraries.sh
./scripts/compile-and-build-images.sh
./scripts/deploy-and-run-tests.sh
