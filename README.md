# Automation-Rest-Assured

## Project Overview

This repository contains API tests for Restful Booker Platform APIs using Postman and REST-assured (Java) - https://automationintesting.online. The Postman collections were initially developed for exploring the API and validating key endpoints, and now serve as a lightweight and fast-check tool alongside the main REST-Assured test suite.

## Getting Started

Follow these steps to set up the project and run the tests.

### 1. Clone the Repository

```bash
git clone https://github.com/mdziegielewska/Automation-Rest-Assured.git
cd Restful-Booker-Platform
```

### 2. Prerequisites

Ensure you have the following installed and correctly configured:

* **Java Development Kit (JDK):** This project is configured to use **Java 22**.
    * Verify your active JDK version using `java -version` and `javac -version`.
    * It is highly recommended to use a tool like [SDKMAN!](https://sdkman.io/) for managing Java versions (e.g., `sdk install java 22-amzn`, `sdk default java 22-amzn`).
* **Apache Maven:** This project requires **Maven 3.9.6 or newer**.
    * Verify your active Maven version: `mvn -version`.
    * If using SDKMAN!, install and set as default (e.g., `sdk install maven`, `sdk default maven <version>`).

    **Important Environment Note:** Maven heavily relies on the `JAVA_HOME` environment variable. Ensure `JAVA_HOME` points to your JDK 22 installation. If you use environment managers like `conda`, ensure they are deactivated (`conda deactivate`) or configured not to override `JAVA_HOME` when running Maven commands.

### 3. Running REST-Assured (Java) Tests

Once prerequisites are met, navigate to the project root (`Restful-Booker-Platform/`) in your terminal.

* **Clean and Build All:**
    ```bash
    mvn clean install
    ```
    This command compiles the project, resolves dependencies, and executes all unit tests.

* **Run Tests Only:**
    ```bash
    mvn test
    ```

### 4. Running Postman Tests

1.  **Install Postman:** Download and install from https://www.postman.com/downloads/.
2.  **Import Collections:** Import the provided `.postman_collection.json` files into your Postman workspace.
3.  **Execute Tests:** Open the Collection Runner, select the desired collection, and initiate the run.


## Technologies Used

* [Postman](https://www.postman.com/)
* [REST-Assured](https://rest-assured.io/) (Java)
* [JUnit 5](https://junit.org/junit5/)
* [Apache Maven](https://maven.apache.org/)
* [Jackson Databind](https://github.com/FasterXML/jackson-databind) (for JSON mapping)
* [AssertJ](https://assertj.github.io/doc/) (for fluent assertions)

## Test Coverage

_to be done_

## Future Enhancements

* Expand Postman collections with negative test scenarios.
* Implement additional REST-Assured test cases for broader API coverage.
* Add reporting capabilities (e.g., Allure Report).