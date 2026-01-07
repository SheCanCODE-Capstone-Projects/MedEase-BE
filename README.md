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

### CI/CD Pipeline with GitHub Actions & Railway

This project includes a complete CI/CD pipeline that automatically:
- ✅ Runs tests on every push/PR
- ✅ Builds Docker image
- ✅ Deploys to Railway on push to `main` branch
- ✅ Uses MongoDB Atlas for managed database
- ✅ Securely manages environment variables

**Quick Setup:**
1. See [QUICK_START.md](./QUICK_START.md) for a 5-minute setup guide
2. See [DEPLOYMENT.md](./DEPLOYMENT.md) for detailed deployment instructions

**Deployment Platforms:**
- ✅ Docker (containerized)
- ✅ Railway (production hosting)
- ✅ MongoDB Atlas (managed database)

### Local Development

1. **Clone the Repository**
   ```bash
   git clone <repository-url>
   cd MedEase-BE
   ```

2. **Configure Environment Variables**
   - Copy `.env.example` to `.env` (if available)
   - Set required environment variables (see DEPLOYMENT.md)

3. **Run the Application**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access Swagger UI**
   - Open http://localhost:8091/swagger-ui.html

### 🐳 Running with Docker

```bash
# Build image
docker build -t medease-backend .

# Run container
docker run -p 8091:8091 \
  -e JWT_SECRET=your-secret \
  -e DB_URI=your-mongodb-uri \
  -e GOOGLE_CLIENT_ID=your-client-id \
  -e MAIL_USERNAME=your-email \
  -e MAIL_PASSWORD=your-password \
  medease-backend
```

## 🧪 Running Tests

```bash
./mvnw test
```

