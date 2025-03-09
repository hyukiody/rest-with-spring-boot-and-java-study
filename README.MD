Restful database with SpringBoot STUDY project;

Based on the "Aprenda Spring Boot framework Swagger Docker Kubernetes K8s API 
RESTful JWT JUnit 5 Mockito React JS do 0 à AWS e GCP e+", online course 
by Leandro Costa at Udemy.com;

This is a project study analysis mirrored on the mentioned online course, 
which focuses its observations onto the structure and relationship of the 
libraries being employed and the communicaions between abstract layers and 
other examples among these libraries and others employed external libraries;

This project was completely transcripted by hand while watching the online 
classes and through the source code provided by the owner of the web course; 
since I find this the only way for me to assimilate the content, which is 
manually copying and explaining the code with commentaries. I chose the 
project for the Spring and MySQL technologies and architecture employed; 
although while progressing with the classes, the owner added the usage of 
other cloud hosting platforms which are not the main focus of this  personal 
study.

Thank you so much for reading.

The following is a more general information specifically about the structure 
of this project, I tried to conceptually separate the structure of the code
into a series of abstract layers:

1. Data Model Layer:
        This is the layer of Entities and their services and methods;
        JPA (Java Persistence API) annotations (@Entity, @Id, etc.) 
        map these objects to your MySQL database tables.
        This is the layer that defines the relationships between Java 
        objects and their aggregations, instead of raw SQL queries.


2. Data Access Layer:
        Spring Data JPA repositories: These interfaces extend JpaRepository 
        and provide methods for common database operations 
        (CRUD - Create, Read, Update, Delete).
        This layer hides the details of database interactions, making your 
        code cleaner.
        A pretty standard set of methods which allows the communication between
        the API's own structure and the development of more complex endpoints.


3. Business Logic Layer:
        Service classes: These contain the core logic of the API, but still
        as native object oriented definitions.
        Dependency Injection: Spring injects the necessary repositories into 
        these services.
        Abstraction: This layer separates business rules from data access and presentation.


4. Presentation Layer (API Layer):
        REST Controllers: These classes handle incoming HTTP requests.The 
        presentation layer is the "face" of the API. It's what clients see and 
        interact with. It is the layer that makes the backend code usable by 
        external applications.
        Spring Web MVC annotations (@GetMapping, @PostMapping, etc.) bind HTTP 
        methods to specific controller methods.
        JSON Serialization/Deserialization: Spring automatically converts 
        Java objects to JSON and vice versa.
        Abstraction: This layer handles the complexities of HTTP and JSON, 
        providing a clean interface for your API.


5. Security Layer:
        Spring Security: Handles authentication and authorization. It utilizes
        the own MySql database as a repository for the privilege registry 
        of the inserted admins or common client users.
        JWT (JSON Web Tokens): Enables secure token-based authentication.
        Abstraction: This layer simplifies the implementation of security 
        features.


6. Infrastructure Layer:
        Web Server (Tomcat, etc.): Hosts the application.
        MySQL Database: Data storage.
        Network: Enables communication.
        Abstraction: This layer hides the underlying infrastructure details.
        Serialization/Deserialization: The conversion of Java objects 
        to JSON (and back) is crucial for the API's communication. Jackson library
        handles this, hence it's implementation.
        Validation: Ensuring that incoming data is valid is essential. Java Bean Validation and Spring Validation can help.
        Exception Handling: Proper error handling is vital for providing 
        informative responses to API client.
       	API Documentation: Tools like Swagger/OpenAPI auto generates the API documentation.


In essence This modular approach makes the code: Easier to maintain,
more testable and more scalable.
