# Various unit tests of spring framework
This repository contains a backend application for managing work schedules, developed as a learning project to practice working with JUnit, Mockito, and the Spock framework. The primary focus of this project is to demonstrate unit testing of various components such as services, mappers, controllers, and repositories.

## Table of Contents
* [Project Overview](#Project-Overview)
* [Technologies Used](#Technologies-Used)
* [Setup and Installation](#Setup-and-Installation)
* [Project Structure](#Project-Structure)
* [Testing](#Testing)
  * [JUnit & Mockito](#JUnit-&-Mockito)
  * [Spock Framework](#Spock-Framework)
* [Contributing](#Contributing)
* [License](#License)

## Project Overview
This project serves as a practical exercise in writing unit tests to ensure the reliability and correctness of the backend logic.

## Technologies Used
* Java 17
* Spring Boot 3.x
* H2-database
* Hibernate
* Mapstruct
* Projectlombok
* JUnit 5
* Mockito
* Spock Framework
* Maven

## Setup and Installation
Clone the repository:

```
git clone https://github.com/your-username/work-schedule-management-backend.git
cd work-schedule-management-backend
```
Build the project:
```
mvn clean install
```
Run the application:
```
mvn spring-boot:run
```
## Project Structure
```
LearTest/
│
├── src/main/java/ivan/denysiuk/learntest
│   ├── annotation
│   ├── bootstrap
│   ├── controllers
│   ├── domains
│   │   ├── dtos
│   │   ├── entities
│   │   ├── mappers
│   │   └── HoursClass.java
│   ├── repositories
│   ├── services
│   ├── validators
│   └── LearnTestApplication.java
│
├── src/test/java/ivan/denysiuk/learntest
│   ├── controllers
│   ├── domains
│   │   └── mappers
│   ├── repositories
│   └── services
│
├── src/test/groovy/ivan/denysiuk/learntest
│   ├── controllers
│   ├── domains
│   │   └── mappers
│   ├── repositories
│   └── services
│
├── pom.xml
└── README.md
```
## Testing
This project includes extensive unit tests for controllers, services, mappers, and repositories. The tests are written using both JUnit & Mockito and the Spock framework to cover different testing strategies and styles.

### JUnit & Mockito
JUnit and Mockito are used to write traditional unit tests in Java. These tests are located under src/test/java/com/yourcompany/schedulemanager.

Example command to run JUnit tests:

```
mvn test
```
### Spock Framework
The Spock framework is used for writing more expressive and readable tests in Groovy. These tests are located under src/test/groovy/com/yourcompany/schedulemanager.

Example command to run Spock tests:

```
mvn test
```
## Contributing

Contributions are welcome! Please fork this repository and submit pull requests with any improvements or bug fixes. Make sure to follow the existing coding style and include appropriate unit tests.

## License
This project is licensed under the MIT License. See the LICENSE file for details.
