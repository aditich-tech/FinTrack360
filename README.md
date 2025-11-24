# FinTrack360

FinTrack360 is a comprehensive personal finance management web application designed to help users track their income, expenses, budgets, and financial goals. It features a secure user authentication system, a dynamic dashboard for users, and an administrative console for system management.

## Features

### User Features
-   **Dashboard**: Real-time overview of financial status, including total balance.
-   **Income & Expense Tracking**: Add and view income sources and daily expenses.
-   **Budget Management**: Create and monitor budgets for specific categories and time periods.
-   **Financial Goals**: Set savings goals and track progress towards them.
-   **Visualizations**: Interactive charts to visualize financial data (powered by Chart.js).
-   **Secure Login**: User authentication with password hashing.

### Admin Features
-   **Admin Dashboard**: Overview of system statistics (Total Users, Expenses, Budgets).
-   **User Management**: View all registered users and delete accounts if necessary.
-   **Security Logs**: Monitor system events and security logs (Login attempts, etc.).

## Technology Stack

### Backend
-   **Java Servlets**: Core application logic and API endpoints.
-   **JDBC**: Database connectivity and operations.
-   **MySQL**: Relational database for data persistence.
-   **Gson**: JSON serialization/deserialization.
-   **BCrypt**: Secure password hashing.
-   **Maven**: Project build and dependency management.

### Frontend
-   **React**: UI library for building dynamic user interfaces (loaded via CDN).
-   **Babel**: JSX compilation in the browser (loaded via CDN).
-   **Chart.js**: Data visualization library (loaded via CDN).
-   **Vanilla CSS**: Custom styling with glassmorphism effects.

## Project Structure

```
FinTrack360/
├── src/
│   ├── main/
│   │   ├── java/com/fintrack/
│   │   │   ├── controller/   # Servlets (API Endpoints)
│   │   │   ├── dao/          # Data Access Objects
│   │   │   ├── model/        # Java Beans / Entities
│   │   │   ├── service/      # Business Logic Layer
│   │   │   └── util/         # Utility classes (DB Connection)
│   │   ├── resources/
│   │   │   └── schema.sql    # Database initialization script
│   │   └── webapp/
│   │       ├── css/          # Stylesheets
│   │       ├── js/           # React Application Logic
│   │       ├── WEB-INF/      # Web Configuration
│   │       └── index.html    # Main Entry Point
├── pom.xml                   # Maven Configuration
└── README.md                 # Project Documentation
```

## Setup & Installation

### Prerequisites
-   Java Development Kit (JDK) 8 or higher.
-   Apache Maven.
-   MySQL Server.

### Database Setup
1.  Open your MySQL client (Workbench, CLI, etc.).
2.  Create the database and tables using the provided script:
    -   Run the contents of `src/main/resources/schema.sql`.
    -   This will create the `fintrack360` database and necessary tables.
    -   It also inserts a default Admin account.

### Configuration
1.  Navigate to `src/main/java/com/fintrack/util/DBConnection.java` (or equivalent utility class).
2.  Update the database credentials (`url`, `username`, `password`) to match your local MySQL setup.

### Running the Application
1.  Open a terminal in the project root directory.
2.  Run the following command to build and start the application using the Cargo Maven plugin:
    ```bash
    mvn clean package cargo:run
    ```
3.  The application will start on the embedded Tomcat server.

### Accessing the App
-   **URL**: `http://localhost:8082/FinTrack360`
-   **Default Admin Credentials**:
    -   Email: `admin@fintrack.com`
    -   Password: `password`

## Security
-   **Password Hashing**: All user passwords are hashed using BCrypt before storage.
-   **Role-Based Access Control**: Separate views and API access for Users and Admins.
-   **SQL Injection Prevention**: Usage of `PreparedStatement` in DAOs.
