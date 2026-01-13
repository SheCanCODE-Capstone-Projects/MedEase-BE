# CI/CD Pipeline Setup Guide

## Overview
This project uses a fully automated CI/CD pipeline that meets all requirements:
- ✅ GitHub Actions for CI/CD
- ✅ Automatic deployment to Railway
- ✅ MongoDB Atlas integration
- ✅ Secure environment variable management
- ✅ Zero manual intervention required

## Pipeline Architecture

### 1. Trigger Conditions
- **Push to main/master branch** → Full pipeline (test → build → deploy)
- **Pull requests** → Test and build only (no deployment)

### 2. Pipeline Stages

#### Stage 1: Test
- Sets up Java 21 environment
- Runs Maven tests with isolated test configuration
- Uses in-memory MongoDB for testing
- Uploads test results as artifacts

#### Stage 2: Build
- Builds application using Maven
- Creates executable JAR file
- Uploads build artifacts

#### Stage 3: Deploy (main/master only)
- Installs Railway CLI
- Authenticates using secure token
- Deploys application automatically
- Verifies deployment completion

## Required Setup

### 1. GitHub Secrets
Add these secrets in your GitHub repository (Settings → Secrets and variables → Actions):

```
RAILWAY_TOKEN=your_railway_token_here
RAILWAY_SERVICE_ID=your_railway_service_id_here
```

### 2. Railway Environment Variables
Set these in your Railway service dashboard:

```env
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/medease?retryWrites=true&w=majority
JWT_SECRET=your-super-secret-jwt-key-at-least-32-characters-long
JWT_EXPIRATION=86400000
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-specific-password
WEBSOCKET_ALLOWED_ORIGINS=https://your-frontend-domain.com
```

### 3. MongoDB Atlas Setup
1. Create MongoDB Atlas account
2. Create cluster and database
3. Create database user with read/write permissions
4. Get connection string and add to Railway as `MONGODB_URI`

## How It Works

### Automatic Deployment Flow
1. **Code Push** → Triggers GitHub Actions
2. **Tests Run** → Validates code quality
3. **Build Process** → Creates deployable JAR
4. **Railway Deploy** → Automatic deployment using Dockerfile
5. **Live Application** → Available on Railway URL

### Security Features
- ✅ No secrets in repository
- ✅ Environment variables for all sensitive data
- ✅ Separate test and production configurations
- ✅ Non-root Docker container execution

### Configuration Files
- **`.github/workflows/deploy.yml`** → CI/CD pipeline definition
- **`railway.json`** → Railway deployment configuration
- **`Dockerfile`** → Multi-stage build for production
- **`application.properties`** → Production configuration with env vars
- **`application-test.yml`** → Isolated test configuration

## Verification Checklist

After setup, verify these work automatically:

- [ ] Push to main/master triggers pipeline
- [ ] Tests pass in GitHub Actions
- [ ] Build creates JAR successfully
- [ ] Deployment completes without errors
- [ ] Application starts on Railway
- [ ] MongoDB Atlas connection works
- [ ] All environment variables load correctly

## Monitoring

### GitHub Actions
- View pipeline status in Actions tab
- Check test results and build logs
- Monitor deployment success/failure

### Railway Dashboard
- Application logs and metrics
- Environment variable management
- Deployment history and rollbacks

### MongoDB Atlas
- Database connection monitoring
- Performance metrics
- Security and access logs

## Troubleshooting

### Common Issues
1. **Pipeline Fails** → Check GitHub Actions logs
2. **Deployment Fails** → Verify Railway token and service ID
3. **App Won't Start** → Check Railway logs for environment variable issues
4. **Database Connection** → Verify MongoDB Atlas URI and network access

### Quick Fixes
- **Secrets Missing** → Add to GitHub repository secrets
- **Environment Variables** → Update in Railway dashboard
- **Build Errors** → Check Java 21 compatibility
- **Test Failures** → Review test configuration

This setup ensures reliable, repeatable deployments with zero manual intervention required.