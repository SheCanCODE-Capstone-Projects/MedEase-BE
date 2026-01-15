# MedEase CI/CD Deployment

## Overview
Automated deployment pipeline using GitHub Actions → Railway → MongoDB Atlas

## Architecture
- **Source**: GitHub Repository
- **CI/CD**: GitHub Actions
- **Hosting**: Railway
- **Database**: MongoDB Atlas
- **Secrets**: Environment Variables

## Setup Instructions

### 1. MongoDB Atlas
1. Create cluster at https://cloud.mongodb.com
2. Create database user
3. Get connection string: `mongodb+srv://user:pass@cluster.mongodb.net/MedEase`

### 2. Railway Setup
1. Create account at https://railway.app
2. Create new project from GitHub repo
3. Get Railway token from dashboard

### 3. GitHub Secrets
Add in repository Settings → Secrets:
```env
MONGODB_URI=mongodb+srv://user:pass@cluster.mongodb.net/MedEase
JWT_SECRET=your_jwt_secret_min_256_bits
```


### 4. Additional Railway Environment Variables
Set these in the Railway dashboard:
```env
GOOGLE_CLIENT_ID=your_google_client_id
MAIL_USERNAME=your_email
MAIL_PASSWORD=your_app_password
```

## Deployment Process
1. Push to `main` branch
2. GitHub Actions triggers
3. Railway builds Java application
4. Deploys with environment variables
5. Connects to MongoDB Atlas
6. Application live at Railway URL

## Files
- `.github/workflows/main.yml` - CI/CD pipeline
- `railway.json` - Railway configuration
- `nixpacks.toml` - Build configuration
- `application.properties` - Environment variables

## Access
- **App**: https://your-app.railway.app
- **Swagger**: https://your-app.railway.app/swagger-ui.html