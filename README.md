#  MedEase – Backend System

MedEase is a **digital healthcare platform backend** designed to streamline **patient flow, doctor consultations, digital prescriptions, and pharmacy dispensing** using secure, real-time workflows.

This repository contains the **backend services** powering authentication, queue management, medical records, digital prescriptions, and pharmacy operations.

---

##  Project Overview

MedEase solves real healthcare challenges such as:
- Long patient waiting times
- Manual and error-prone prescriptions
- Poor coordination between doctors and pharmacists
- Lack of real-time queue visibility

By digitizing the full medical workflow, MedEase ensures:
- Faster service delivery  
- Secure digital prescriptions  
- Accurate patient records  
- Efficient pharmacy dispensing  

---

##  Core Features (Backend)

1.  User Authentication (Patient, Doctor, Pharmacist)
2.  Role-Based Access Control (RBAC)
3.  Patient Profile & Reference Number System
4.  Digital Patient Queue Management
5. Doctor Consultation & Medical Records
6.  Digital Prescriptions
7.  One-Time QR Code for Secure Dispensing
8.  Pharmacy Prescription Verification & Dispensing
9.  Prescription Status Tracking (Active / Used / Expired)
10.  Audit Logs for Medical Actions
11.  Notification System (In-App / Future SMS Support)

---

## 🛠️ Tech Stack

- **Language:** Java
- **Framework:** Spring Boot
- **Security:** Spring Security + JWT
- **Database:** PostgreSQL / MongoDB
- **ORM:** Hibernate / JPA / Mongoose
- **Build Tool:** Maven
- **Containerization:** Docker & Docker Compose
- **Version Control:** Git & GitHub
- **Project Management:** GitHub Issues & Projects

---
## 📄 API Documentation

we use Swagger UI for the the documentation

## 🚀 Deployment 

### Automated CI/CD Pipeline

The application uses a fully automated CI/CD pipeline with:
- **CI/CD Platform**: GitHub Actions
- **Deployment Platform**: Railway
- **Database**: MongoDB Atlas (managed)
- **Triggers**: Automatic deployment on push/merge to `main` branch

#### Pipeline Stages:
1. **Test**: Runs unit tests with test configuration
2. **Build**: Creates executable JAR using Maven
3. **Deploy**: Deploys to Railway using Docker

#### Required Setup:

**GitHub Secrets** (Repository Settings > Secrets):
- `RAILWAY_TOKEN`: Railway authentication token
- `RAILWAY_SERVICE_ID`: Railway service identifier

**Railway Environment Variables**:
- `MONGODB_URI`: MongoDB Atlas connection string
- `JWT_SECRET`: JWT signing key (minimum 32 characters)
- `MAIL_USERNAME` & `MAIL_PASSWORD`: Email service credentials
- `GOOGLE_CLIENT_ID`: Google OAuth client ID
- `WEBSOCKET_ALLOWED_ORIGINS`: Frontend domain URLs

#### Deployment Process:
1. Push code to `main` branch
2. GitHub Actions automatically runs tests and builds
3. Railway deploys the application using Docker
4. Application is available at the Railway-provided URL

📖 **Detailed deployment guide**: See [DEPLOYMENT.md](DEPLOYMENT.md)

### Manual Setup
### 1️⃣ Clone this Repository
### 2️⃣ Configure Database
### 3️⃣ Run the Application
## 🐳 Running with Docker (Recommended)
## 🧪 Running Tests

