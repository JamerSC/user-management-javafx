### Java Desktop Application
### Use
r Management System
#### 8/17/2026

Architecture Overview
```
              Java Desktop Application
                         │
                         ▼
              ┌─────────────────────┐
              │     UI / GUI        │
              │ JavaFX / Swing      │
              └──────────┬──────────┘
                         │
                         ▼
              ┌─────────────────────┐
              │      Service        │
              │   Business Logic    │
              └──────────┬──────────┘
                         │
                         ▼
              ┌─────────────────────┐
              │        DAO          │
              │      JDBC / SQL     │
              └──────────┬──────────┘
                         │
                         ▼
              ┌─────────────────────┐
              │       MySQL         │
              └─────────────────────┘
```
#### User Management Desktop App


![image_1.png](images%2Fimage_1.png)

![image_2.png](images%2Fimage_2.png)

![image_3.png](images%2Fimage_3.png)

![image_4.png](images%2Fimage_4.png)

![image_3.png](images%2Fimage_5.png)

### Java Core & Spring Boot (Java Framework) Comparison
```
Java Core                     Spring Boot
------------------------------------------------
Main                    →     Controller
UserService             →     Service
UserDAO                 →     Repository
JDBC                    →     JPA/Hibernate
DatabaseConnection      →     DataSource
User                    →     Entity
```

```
The purpose of the provided code and structure is to implement a User Management System using a layered architecture with JavaFX for the user interface, Java for the backend logic, and MySQL for data storage. Each layer has a specific responsibility:  
FXML (View Layer): Defines the graphical user interface (GUI) layout using XML. It separates the UI design from the application logic.  
Controller Layer: Handles user interactions (e.g., button clicks) and communicates with the service layer to perform actions like adding, updating, or deleting users.  
Service Layer: Contains the business logic of the application. It validates input and interacts with the DAO layer to perform database operations.  
DAO (Data Access Object) Layer: Manages database operations such as querying, inserting, updating, and deleting user data in the MySQL database.  
Database Connection Layer: Provides a reusable connection to the MySQL database.
```

```
Implement a robust, production-ready login system in JavaFX, you should follow four key architectural principles:

1. Centralized View Navigation (ViewManager): Views should not instantiate other views directly. Use a central manager to handle scene switches and stage resizes.
2. Asynchronous Execution (Task): Database and authentication calls must run off the JavaFX Application Thread using background threads or Task<T> to prevent the UI from freezing.
3. Session Management (UserSession): Keep track of the currently logged-in user in a thread-safe session context.
4. Clean Event Handling: Avoid duplicating event listeners (your current LoginUI sets loginButton.setOnAction twice).
```

Developer: JamerSC
Note: Recap, Refresh, & Practice Java Core