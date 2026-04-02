#         NexaVault - Modern Fintech Banking Platform

<div align="center">

**A full-stack fintech application for seamless account management, transfers, and expense tracking**

[![Live Demo](https://img.shields.io/badge/demo-online-success?style=for-the-badge&logo=vercel)](https://nexavault-frontend.onrender.com)
[![GitHub](https://img.shields.io/badge/github-repository-blue?style=for-the-badge&logo=github)](https://github.com/nischithakrishna6/payment-platform)

</div>

---

##  Table of Contents

- [About The Project](#about-the-project)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Installation](#installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Docker Deployment](#docker-deployment)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
---

##  About The Project

NexaVault is a modern, full-stack fintech banking platform that provides users with comprehensive financial management tools. Built with cutting-edge technologies, it offers secure account linking, seamless fund transfers, and intelligent expense tracking with visual analytics.

### Why NexaVault?

-  **Bank-Grade Security** - JWT authentication with encrypted data transmission
-  **Lightning Fast** - Optimized performance with lazy loading and caching
-  **Responsive Design** - Seamless experience across all devices
-  **Smart Analytics** - Visual insights into spending patterns
-  **Modern UI/UX** - Glassmorphism design with smooth animations
-  **Cloud Native** - Containerized and deployed on modern cloud infrastructure

---

##  Features
LOGIN PAGE
<img width="1894" height="863" alt="Screenshot 2026-04-01 221256" src="https://github.com/user-attachments/assets/9ede58f3-f529-4a19-8389-d3d1a79013fe" />
REGISTER PAGE
<img width="1899" height="866" alt="Screenshot 2026-04-01 221336" src="https://github.com/user-attachments/assets/31fdb603-4510-4ef3-b005-2f4b70c8d90a" />
DASHBOARD
<img width="1896" height="868" alt="Screenshot 2026-04-02 222612" src="https://github.com/user-attachments/assets/1c801bcc-03ef-44ff-a33b-bb849bf8801a" />
ADD ACCOUNTS
<img width="1896" height="867" alt="Screenshot 2026-04-02 222648" src="https://github.com/user-attachments/assets/e14de80e-b091-4a4d-8b9b-cfc6eb1dd99d" />
TRANSFER MONEY
<img width="1891" height="873" alt="Screenshot 2026-04-02 222657" src="https://github.com/user-attachments/assets/cd14dbc2-f0a9-4243-91fb-4f5aa36c5e59" />
TRANSACTION HISTORY
<img width="1892" height="861" alt="Screenshot 2026-04-02 222711" src="https://github.com/user-attachments/assets/653fed91-a991-4752-9d48-969536902f8d" />
EXPENSE TRACKER
<img width="1888" height="847" alt="Screenshot 2026-04-02 222948" src="https://github.com/user-attachments/assets/83bbcdef-ee49-406d-87d6-77b3a4ce9a02" />
FORGOT PASSWORD PAGE
<img width="1900" height="740" alt="Screenshot 2026-04-01 222615" src="https://github.com/user-attachments/assets/d29a9709-178d-4fc0-871f-2fee3dea87b9" />
FOOTER
<img width="1901" height="86" alt="Screenshot 2026-04-01 222257" src="https://github.com/user-attachments/assets/cf39891d-84b1-4b13-a8dc-6150a86a538e" />



###  Authentication & Security
- JWT-based authentication system
- Secure password encryption (BCrypt)
- OTP-based password reset via email
- Session management with auto-logout

###  Account Management
- Link multiple bank accounts
- Real-time IFSC code validation
- Account verification system
- Support for Savings, Current, and Business accounts
- ATM-style card visualization

###  Fund Transfers
- Instant transfers between linked accounts
- Transaction history with filters
- Transfer confirmation system
- Real-time balance updates

###  Expense Tracking
- Category-based expense management
- Interactive charts (Pie & Line charts)
- Daily/Weekly/Monthly analytics
- 10+ expense categories with custom icons
- Export capabilities

###  User Interface
- Glassmorphism design aesthetic
- Responsive layout (mobile, tablet, desktop)
- Dark-themed components
- Smooth animations and transitions
- ATM card-style account display with stacking/grid views

---

##  Tech Stack

### Frontend
![Angular](https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-007ACC?style=for-the-badge&logo=typescript&logoColor=white)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![Chart.js](https://img.shields.io/badge/Chart.js-FF6384?style=for-the-badge&logo=chartdotjs&logoColor=white)

- **Angular 19** - Progressive web framework
- **TypeScript** - Typed JavaScript
- **RxJS** - Reactive programming
- **Chart.js** - Data visualization
- **Angular Router** - Client-side routing
- **Reactive Forms** - Form handling

### Backend
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

- **Spring Boot 3.4.1** - Java framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Data persistence
- **PostgreSQL** - Relational database
- **Hibernate** - ORM framework
- **JWT** - Secure token-based authentication
- **JavaMailSender** - Email service
- **Lombok** - Boilerplate code reduction

### DevOps & Deployment
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Render](https://img.shields.io/badge/Render-46E3B7?style=for-the-badge&logo=render&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)

- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **Render** - Cloud platform
- **Nginx** - Web server (frontend)
- **Maven** - Build automation
- **Git** - Version control

---

##  Getting Started

### Prerequisites

Ensure you have the following installed:

- **Node.js** (v20 or higher)
- **Java JDK** (v21 or higher)
- **PostgreSQL** (v16 or higher)
- **Docker** (optional, for containerized deployment)
- **Git**
```bash

##  Installation

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/nischithakrishna6/payment-platform.git
cd payment-platform
```

### 2️⃣ Backend Setup
```bash
cd backend

# Configure database
# Edit src/main/resources/application.properties
# Update database credentials

# Run backend
./mvnw spring-boot:run

# Backend will run on http://localhost:8081
```

### 3️⃣ Frontend Setup
```bash
cd frontend

# Install dependencies
npm install

# Start development server
ng serve

# Frontend will run on http://localhost:4200
```

### 4️⃣ Database Setup
```sql
-- Create database
CREATE DATABASE fintechdb;

-- Tables will be auto-created by Hibernate on first run
```

---

## Docker Deployment

### Quick Start with Docker Compose
```bash
# Build and run all services
docker-compose up --build

# Run in detached mode
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Services

- **Frontend**: http://localhost
- **Backend**: http://localhost:8081
- **PostgreSQL**: localhost:5432

---

##  Usage

### 1️ Register Account

1. Navigate to the registration page
2. Fill in email, password, and personal details
3. Submit to create account

### 2️ Link Bank Account

1. Login with credentials
2. Click "Add Account"
3. Select your bank
4. Enter IFSC code and verify
5. Enter account details
6. Demo balance (₹5,000 - ₹50,000) will be assigned

### 3️ Transfer Funds

1. Navigate to "Transfer Money"
2. Select source and destination accounts
3. Enter amount and description
4. Confirm transfer

### 4️ Track Expenses

1. Go to "Track Expenses"
2. Add expenses with category, amount, and date
3. View analytics (daily/weekly/monthly)
4. Visualize spending patterns

---

##  API Documentation

### Base URL
```
Production: https://nexavault-backend.onrender.com/api
Local: http://localhost:8081/api
```


## 📁 Project Structure
```
payment-platform/
├── backend/                      # Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/fintech/platform/
│   │   │   │       ├── config/           # Configuration classes
│   │   │   │       ├── controller/       # REST controllers
│   │   │   │       ├── dto/              # Data Transfer Objects
│   │   │   │       ├── model/            # JPA entities
│   │   │   │       ├── repository/       # Data repositories
│   │   │   │       ├── security/         # Security components
│   │   │   │       └── service/          # Business logic
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── application-prod.properties
│   │   └── test/                         # Unit tests
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                     # Angular frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/       # UI components
│   │   │   ├── services/         # API services
│   │   │   ├── models/           # TypeScript interfaces
│   │   │   ├── guards/           # Route guards
│   │   │   └── interceptors/     # HTTP interceptors
│   │   ├── environments/         # Environment configs
│   │   └── styles.css            # Global styles
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── angular.json
│   └── package.json
│
├── docker-compose.yml            # Multi-container orchestration
├── render.yaml                   # Render deployment config
└── README.md                     # This file

```

---

## 🌟 Key Features Explained

### JWT Authentication Flow
```
User Login → Credentials Validation → JWT Token Generation → 
Token Storage → Authenticated Requests → Token Verification → 
Access Granted
```

### Transfer Process
```
Select Accounts → Validate Balance → Create Transaction → 
Update Balances → Record History → Send Confirmation
```

### Expense Analytics
```
Add Expense → Categorize → Store Data → 
Generate Stats → Visualize Charts → Export Reports
```

---

## 🔒 Security Features

- **Password Encryption**: BCrypt hashing (strength 12)
- **JWT Tokens**: Secure, stateless authentication
- **CORS Configuration**: Controlled cross-origin requests
- **SQL Injection Prevention**: Parameterized queries via JPA
- **XSS Protection**: Input sanitization
- **CSRF Protection**: Disabled for stateless API
- **HTTPS**: Enforced in production (Render SSL)

---

## 🤝 Contributing

Contributions make the open-source community amazing! Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

