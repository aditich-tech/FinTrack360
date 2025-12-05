# FinTrack360

FinTrack360 is a comprehensive, secure, and advanced personal finance management web application. It combines a robust Java backend with a modern, futuristic "Holographic HUD" React frontend to help users track income, expenses, budgets, and financial goals with ease.

The application features **stateless authentication (JWT)**, **AES-128 data encryption**, and **automated recurring transactions**, making it a secure and powerful tool for financial tracking.

---

## Key Features

### Robust Security
-   **Stateless Authentication**: Implements **JSON Web Tokens (JWT)** for secure, scalable session management without server-side state.
-   **Data Encryption**: Sensitive financial data (amounts, descriptions) is encrypted **at rest** in the database using **AES-128** encryption.
-   **Secure Storage**: User passwords are hashed using **BCrypt** before storage.
-   **Access Control**: Strict Role-Based Access Control (RBAC) separates User and Admin functionalities.
-   **SQL Injection Prevention**: All database operations use `PreparedStatement` to prevent injection attacks.

### Financial Management (User)
-   **Holographic Dashboard**: Real-time overview of financial health, total balance, and recent activity.
-   **Income & Expense Tracking**: Log daily transactions with source/category and descriptions.
-   **Recurring Transactions**: **[NEW]** Automate finances by setting up daily, weekly, or monthly recurring income/expenses. A background scheduler processes these automatically.
-   **Budget Management**: Set spending limits for specific categories and track adherence.
-   **Goal Tracking**: Define savings goals (e.g., "New Car") and monitor progress towards the target amount.
-   **Visualizations**: Interactive charts and graphs powered by **Chart.js**.

### Administration (Admin)
-   **Admin Console**: Dedicated dashboard for system oversight.
-   **System Statistics**: View total users, transaction counts, and active budgets.
-   **User Management**: View registered users and manage accounts (delete users).
-   **Security Audit Logs**: Monitor system security events, login attempts, and potential breaches.

### UI/UX
-   **Holographic HUD Theme**: A unique, deep-space inspired interface featuring:
    -   Glassmorphism effects (frosted glass).
    -   Neon Cyan and Pink accents.
    -   "Orbitron" tech typography.
    -   Smooth CSS animations and transitions.
-   **Responsive Design**: Fully responsive layout that adapts to desktop and mobile screens.

---

## Technology Stack

### Backend
-   **Language**: Java 17
-   **Core**: Java Servlets, JDBC
-   **Database**: MySQL 8.0+
-   **Security**: JJWT (Java JWT), BCrypt, AES Encryption
-   **Utilities**: Gson (JSON processing)
-   **Build Tool**: Maven

### Frontend
-   **Library**: React 18 (via CDN)
-   **Compiler**: Babel (in-browser compilation)
-   **Styling**: Vanilla CSS3 (Custom Variables, Flexbox/Grid)
-   **Visualization**: Chart.js
-   **Icons**: FontAwesome

---

## Project Structure

```text
FinTrack360/
├── src/
│   ├── main/
│   │   ├── java/com/fintrack/
│   │   │   ├── controller/   # Servlets (API Endpoints: Auth, Finance, Admin)
│   │   │   ├── dao/          # Data Access Objects (SQL operations)
│   │   │   ├── model/        # Java Beans / Entities (User, Transaction, etc.)
│   │   │   ├── service/      # Business Logic Layer (Security, Validation)
│   │   │   └── util/         # Utilities (DBConnection, Encryption, JWT)
│   │   ├── resources/
│   │   │   └── schema.sql    # Database initialization script
│   │   └── webapp/
│   │       ├── css/          # Custom Stylesheets (style.css)
│   │       ├── js/           # React Application Logic (app.js)
│   │       ├── WEB-INF/      # Web Configuration (web.xml)
│   │       └── index.html    # Main Entry Point
├── pom.xml                   # Maven Configuration
└── README.md                 # Project Documentation
```

---

## Setup & Installation

### Prerequisites
-   **Java Development Kit (JDK)**: Version 17 or higher.
-   **Apache Maven**: For building the project.
-   **MySQL Server**: Local or remote instance.

### 1. Database Setup
The application is designed to **automatically initialize** the database schema on startup.
1.  Ensure your MySQL server is running.
2.  Open `src/main/java/com/fintrack/util/DBConnection.java`.
3.  Update the `url`, `username`, and `password` constants to match your local MySQL configuration.
    *   *Default*: `jdbc:mysql://localhost:3306/`, `root`, `12345678`
4.  **Note**: The `DatabaseInitializer` will create the `fintrack360` database and all required tables if they do not exist.

### 2. Build & Run
1.  Open a terminal in the project root directory.
2.  Execute the following Maven command to clean, package, and run the application using the Cargo plugin (Embedded Tomcat):
    ```bash
    mvn clean package cargo:run
    ```
3.  Wait for the "Tomcat 9.x Embedded started" message.

### 3. Accessing the Application
-   **App URL**: `http://localhost:8082/FinTrack360`
-   **Default Admin Credentials**:
    -   **Email**: `admin@fintrack.com`
    -   **Password**: `password`

---

## API Documentation

### Authentication (`/api/auth/*`)
-   `POST /login`: Authenticate user and return JWT.
-   `POST /register`: Register a new user account.
-   `POST /logout`: Invalidate session (client-side).

### Finance Operations (`/api/finance/*`)
-   `GET /expenses`: Retrieve user expenses.
-   `POST /expenses`: Add a new expense.
-   `GET /incomes`: Retrieve user income sources.
-   `POST /incomes`: Add a new income.
-   `GET /budgets`: Retrieve active budgets.
-   `POST /budgets`: Set a new budget limit.
-   `GET /goals`: Retrieve financial goals.
-   `POST /goals`: Create a new goal.
-   `GET /recurring`: Get active recurring transactions.
-   `POST /recurring`: Schedule a new recurring transaction.
-   `DELETE /recurring`: Stop a recurring transaction.

### Admin Operations (`/api/admin/*`)
-   `GET /stats`: Retrieve system-wide statistics.
-   `GET /users`: List all registered users.
-   `DELETE /users`: Delete a user account.
-   `GET /logs`: View security audit logs.

---

## Security Implementation Details

1.  **Encryption**:
    -   The `EncryptionUtil` class provides AES-128 encryption/decryption.
    -   It is applied to the `amount` and `description` columns in the database.
    -   This ensures that even if the database is compromised, sensitive financial figures remain unreadable without the key.

2.  **JWT Authentication**:
    -   The `JwtUtil` class generates signed tokens containing the user's ID, Email, and Role.
    -   The `JwtAuthFilter` intercepts every request to `/api/*`, validates the token signature, and enforces stateless authentication.

3.  **Scheduler**:
    -   The `TransactionScheduler` is a `ServletContextListener` that initializes a daily timer.
    -   It checks the `recurring_transactions` table for items due "today" and automatically inserts them into the `incomes` or `expenses` tables.
