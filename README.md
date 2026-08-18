# Fitness Tracker - A Cloud-Native Microservices Project

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.x-brightgreen.svg)
![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)
![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)

This project is a comprehensive, full-stack fitness tracking application built from the ground up using a cloud-native microservices architecture. It demonstrates a deep understanding of modern backend development practices, including service discovery, centralized configuration, asynchronous communication, and containerization.

---

## ✨ Key Features

*   **User Authentication & Management**: Secure user registration and profile management.
*   **Activity Tracking**: Log detailed workout data, including activity type, duration, and calories burned.
*   **🤖 AI-Powered Recommendations**: Integrates with the **Google Gemini API** to provide users with personalized post-workout feedback, safety tips, and suggestions for improvement.
*   **🚀 Scalable & Resilient Architecture**: Built on a distributed system of independent services that can be scaled and maintained separately.
*   **Centralized Configuration**: All service configurations are managed externally, allowing for dynamic updates without service restarts.

---

## 🏛️ Architecture Overview

This project follows a classic microservices pattern, with several independent services communicating through a central API Gateway and a service registry.

[//]: # (HIGHLY RECOMMENDED: Create an architecture diagram using a tool like diagrams.net or Excalidraw and embed it here. It's the fastest way to impress.)

**A high-level overview of the request flow:**

1.  A **Client** makes a request to the **API Gateway**.
2.  The **API Gateway** authenticates the request and uses the **Eureka Server** to discover the network location of the target service (e.g., `user-service`).
3.  The request is forwarded to the appropriate service.
4.  Services fetch their configuration from the **Config Server** on startup.
5.  For asynchronous tasks, the `activity-service` publishes an event to a **Kafka** topic.
6.  The `ai-service` consumes this event and processes it independently, calling the Gemini API to generate feedback.

---

## 🛠️ Technology Stack

| Category                     | Technology                                                              |
| ---------------------------- | ----------------------------------------------------------------------- |
| **Backend Framework**        | `Spring Boot`, `Spring WebFlux` (for Gateway)                           |
| **Service Orchestration**    | `Spring Cloud`                                                          |
| &nbsp; &nbsp; ↳ **Gateway**    | `Spring Cloud Gateway`                                                  |
| &nbsp; &nbsp; ↳ **Discovery**  | `Spring Cloud Eureka Server`                                            |
| &nbsp; &nbsp; ↳ **Config**     | `Spring Cloud Config Server`                                            |
| &nbsp; &nbsp; ↳ **Load Balancing** | `Spring Cloud LoadBalancer`                                             |
| **Asynchronous Messaging**   | `Apache Kafka`                                                          |
| **Databases**                | `PostgreSQL` (User Service), `MongoDB` (Activity Service)               |
| **Authentication**           | `Keycloak` (via Spring Security & OAuth2)                               |
| **AI Integration**           | `Google Gemini API`                                                     |
| **Containerization**         | `Docker` & `Docker Compose`                                             |
| **Build & Dependencies**     | `Apache Maven`                                                          |

---

## 💡 Core Concepts Demonstrated

*   **Microservice Architecture**: Decoupling a monolithic application into smaller, independent services.
*   **Service Discovery & Registry**: Using Eureka to allow services to dynamically find and communicate with each other.
*   **API Gateway Pattern**: Providing a single, unified entry point for all client requests, handling routing, and cross-cutting concerns.
*   **Centralized Configuration**: Externalizing application properties using Spring Cloud Config for better management across different environments.
*   **Event-Driven Architecture**: Using Kafka for asynchronous, non-blocking communication between services, enhancing resilience and scalability.
*   **Polyglot Persistence**: Choosing the right database for the right job (SQL for structured user data, NoSQL for flexible activity logs).
*   **Containerization**: Packaging each service into a Docker container for consistent, isolated deployments.

---

## 🚀 Getting Started

This project is fully containerized and can be run easily with Docker Compose.

### Prerequisites

*   Git
*   JDK 17
*   Apache Maven
*   Docker and Docker Compose

### Running the Application

1.  **Clone the repository:**
    ```bash
    git clone <your-repository-url>
    cd FitnessMicroservice
    ```

2.  **Build each microservice:**
    Since this project contains multiple independent Maven projects, you must build each one individually. For each service (`configserver`, `eurekaserver`, `gateway`, etc.), navigate to its directory and run:
    ```bash
    # Example for userservice
    cd userservice
    mvn clean install -DskipTests
    cd ..
    ```
    *(Repeat for all services)*

3.  **Build the Docker images:**
    For each service, navigate to its directory and run the `docker build` command:
    ```bash
    # Example for userservice
    cd userservice
    docker build -t fitness/user-service:latest .
    cd ..
    ```
    *(Repeat for all services, giving each a unique image name)*

4.  **Launch the entire ecosystem with Docker Compose:**
    Make sure you have a complete `docker-compose.yml` file as described in the `Deployment_Guide.md`. Then, run:
    ```bash
    docker-compose up -d
    ```

5.  **Verify the deployment:**
    *   Open the **Eureka Dashboard** at `http://localhost:8761` to see all registered services.
    *   Start making API calls to the **API Gateway** at `http://localhost:8080`.

---

## 📋 API Endpoints

All requests are routed through the API Gateway on port `8080`.

<details>
<summary><strong>User Service Endpoints (`/api/users`)</strong></summary>

*   `POST /api/users/register`: Register a new user.
*   `GET /api/users/fetch/{userId}`: Get a user's profile.
*   `GET /api/users/validate/{userId}`: Check if a user profile exists.

</details>

<details>
<summary><strong>Activity Service Endpoints (`/api/activities`)</strong></summary>

*   `POST /api/activities/add`: Track a new workout activity.

</details>

---

## 🔮 Future Improvements

*   **Implement a CI/CD Pipeline**: Automate the build, test, and deployment process using tools like Jenkins or GitHub Actions.
*   **Deploy to Kubernetes**: Move from Docker Compose to a Kubernetes cluster for production-grade orchestration, auto-scaling, and self-healing.
*   **Add Distributed Tracing**: Use tools like Zipkin or Jaeger to trace requests across multiple microservices for easier debugging.
*   **Enhance Monitoring**: Integrate Prometheus and Grafana to collect metrics and visualize the health and performance of the services.

---

## 📄 License

This project is distributed under the MIT License. See `LICENSE` for more information.
