# DTU Pay — Simple Payment Service (02267 Web Services)

DTU Pay is a **payment platform** for the DTU course **02267 – Web Services**. It showcases simple Java microservices with messaging, tests, and an in-memory setup that resets on restart.

## Highlights

- **RESTful HTTP API** exposed through a façade service
- **Event-driven communication** using RabbitMQ between microservices
- **Test discipline** with Cucumber (BDD) and JUnit
- **DevOps pipeline**: Maven multi-module build, Docker, and Jenkins-ready scripts
- **In-memory design** that keeps the focus on web-service concepts, not persistence

---

## Repository Layout

```
group8-dtupay/
├── account-service/             # Account management microservice
├── end-to-end-tests/            # Cucumber-based end-to-end scenarios
├── facade/                      # Public REST API façade
├── messaging-utilities/         # Shared messaging abstractions
├── payment-service/             # Payment handling logic
├── report-service/              # Reporting
├── token-service/               # Customer token issuance/validation
├── scripts/                     # Helper scripts for build/test/deploy
├── DTUPayProjectDescription.md  # Architecture and design write-up
├── InstallationGuide.md         # Full installation & troubleshooting guide
└── README.md                    # You are here


## Course Information

- Course: **02267 – Web Services**
- Institution: **Technical University of Denmark (DTU)**

---

## Authors

| Name                     | Student ID | Email                    |
| ------------------------ | ---------- | ------------------------ |
| Abinav Reddy Aleti       | s224786    | [s224786@student.dtu.dk] |
| Deniz Isikli             | s215818    | [s215818@student.dtu.dk] |
| Krish Devendra Waghresha | s252788    | [s252788@student.dtu.dk] |
| Parth Mangalkar          | s252576    | [s252576@student.dtu.dk] |
| Utkarsh Rupesh Saner     | s252680    | [s252680@student.dtu.dk] |
| Vaibhav Anil Nagwani     | s242572    | [s242572@student.dtu.dk] |
| Vaibhav Bahel            | s252654    | [s252654@student.dtu.dk] |
