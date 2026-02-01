INSTALLATION GUIDE – DTU PAY
============================

This document describes how to build, deploy, and test the DTU Pay microservice system
from the command line on any Linux machine, without Jenkins and without access to our
virtual machine. The instructions below reflect the exact scripts and Docker setup that
ship with this repository (January 2026).

This installation guide is provided at the root of the submitted ZIP file.


GIT REPOSITORIES
----------------
The source code is available at:

- https://gitlab.gbar.dtu.dk/s252788/group8-dtupay

Access:
- Public read access is enabled OR
- The following accounts have developer access:
    - GitHub: hubertbau
    - GitLab (DTU): huba
    - GitLab.com: huba63


JENKINS PIPELINE
----------------
Continuous integration and deployment for DTU Pay runs on a Jenkins pipeline hosted on our course-provided virtual machine. The Jenkins job invokes the same scripts documented below, ensuring remote evaluators can reproduce the exact build/test workflow.

Jenkins access:
- Jenkins base URL: `http://161.35.200.165:8282/`
- Service account: `huba`
- Password: `Huba@1234!`
- Job name: `dtu-pay-final-version`

Pipeline behavior:
1. Checkout repository from GitLab using the above service account.
2. Execute `scripts/build-and-run.sh` which:
   - Installs shared messaging libraries
   - Builds all services and Docker images
   - Deploys the stack via Docker Compose and runs end-to-end tests



PREREQUISITES
-------------
Install the following dependencies before working with the project:

- Git 2.40+ (to clone the repository)
- Java – OpenJDK 21
- Maven – Apache Maven 3.9.6
- Docker – Docker Engine 25.x
- Docker Compose v2 (bundled with Docker Engine; `docker compose` command)
- Bash-compatible shell (/bin/bash). On Windows, use WSL2 or Git Bash so that the
  provided scripts run without modification.


SHELL SCRIPT CONVENTIONS
-----------------------
All shell scripts in this project:
- Use Unix (LF) line endings
- Are executable for the user (`chmod +x scripts/*.sh` after unzip on Windows)
- Start with a valid shebang (`#!/usr/bin/env bash`)

If the project is cloned on Windows, ensure Unix line endings are preserved:
`git config --global core.autocrlf input`


AUTOMATED QUICK START (FULL PIPELINE)
-------------------------------------
To exercise the full automated pipeline from the project root, run:

```
chmod +x scripts/*.sh   # first run only, if needed
./scripts/build-and-run.sh
```

`build-and-run.sh` chains the three scripts located under `scripts/`:

1. `install-libraries.sh` – `mvn clean install -f messaging-utilities` to publish the
   shared messaging module to the local Maven cache.
2. `compile-and-build-images.sh` – `mvn clean package -pl '!end-to-end-tests'` followed by
   `docker build` for each microservice (`account`, `token`, `payment`, `report`, `facade`).
3. `deploy-and-run-tests.sh` – Detects `docker compose`/`docker-compose`, recreates the
   stack defined in `docker-compose.yml`, waits for services, then executes
   `mvn clean test -f end-to-end-tests/pom.xml`.

When the script finishes you have:
- All microservice images refreshed locally.
- The full stack running in Docker (facade exposed on http://localhost:8080).
- End-to-end system tests executed against the running stack with their results printed
  to the console.


MANUAL WORKFLOW (STEP-BY-STEP)
------------------------------
If you prefer to run the individual steps manually, execute the following from the
repository root:

1. **Install shared libraries**
   ```
   ./scripts/install-libraries.sh
   # or: mvn clean install -f messaging-utilities/pom.xml
   ```

2. **Build services and Docker images**
   ```
   ./scripts/compile-and-build-images.sh
   ```
   This runs the Maven multi-module build (excluding `end-to-end-tests`) and then issues
   `docker build` commands for each microservice Dockerfile.

3. **Start the microservice stack without tests**
   ```
   docker compose -f docker-compose.yml up -d
   ```
   The compose file lives in the repository root and starts:
   - `rabbitmq` (5672/AMQP, 15672/management)
   - `account-service`, `token-service`, `payment-service`, `report-service`
   - `facade` (HTTP API exposed on http://localhost:8080)
   Add the optional `--build` flag only if you skipped step 2 and need Docker to build
   images on the fly (which is slower and not what the scripts do).

4. **Run end-to-end tests (optional outside the exam pipeline)**
   ```
   ./scripts/run-tests.sh
   # equivalent to: mvn clean test -f end-to-end-tests/pom.xml
   ```
   The script ensures the compose stack is recreated before executing the Cucumber-based
   scenarios located in `end-to-end-tests/`. `build-and-run.sh` already executes the same
   tests via `deploy-and-run-tests.sh`, so this step is just for local debugging.


RUNNING TESTS MANUALLY
----------------------
- **Per-module unit/integration tests:** `mvn test -pl <module>` (e.g.,
  `mvn test -pl account-service`).
- **All modules except end-to-end:** `mvn test -pl '!end-to-end-tests'`.
- **End-to-end suite only:** `mvn clean test -f end-to-end-tests/pom.xml` or
  `./scripts/run-tests.sh` (ensures Docker services are up).


STOPPING THE SYSTEM
-------------------
To stop the stack and clean volumes/networks created for this project:

```
docker compose -f docker-compose.yml down --volumes --remove-orphans
```


SUPPORTED PLATFORM
------------------
- Actively tested on Linux (Ubuntu 22.04) with Docker Engine 25.x.
- Verified on macOS 14 with Docker Desktop 4.26+.
- Works on Windows 11 via WSL2 or Git Bash for scripting (Docker Desktop required).
