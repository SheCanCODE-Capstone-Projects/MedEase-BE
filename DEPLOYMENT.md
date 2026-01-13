# MedEase Backend - CI/CD Deployment Guide

## Overview
This project uses a fully automated CI/CD pipeline that deploys the MedEase backend application to Railway using GitHub Actions, with MongoDB Atlas as the managed database.

## Architecture
- **CI/CD Platform**: GitHub Actions
- **Deployment Platform**: Railway
- **Database**: MongoDB Atlas (managed)
- **Build Tool**: Maven
- **Runtime**: Java 21

## Pipeline Workflow

### 1. Trigger Conditions
The pipeline automatically triggers on:
- Push to `main` branch
- Pull requests to `main` branch

### 2. Pipeline Stages

#### Stage 1: Test
- Sets up Java 21 environment
- Runs Maven tests with test profile
- Uses in-memory test configuration
- Uploads test results as artifacts

#### Stage 2: Build
- Builds the application using Maven
- Creates executable JAR file
- Uploads build artifacts

#### Stage 3: Deploy (main branch only)
- Installs Railway CLI
- Authenticates with Railway using token
- Links to the configured service
- Deploys the application
- Verifies deployment completion

## Required Secrets

Configure these secrets in your GitHub repository settings:

### GitHub Secrets
- `RAILWAY_TOKEN`: Your Railway authentication token
- `RAILWAY_SERVICE_ID`: Your Railway service identifier

### Railway Environment Variables
Set these in your Railway service dashboard:

```env
# Database
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/medease?retryWrites=true&w=majority

# JWT Configuration
JWT_SECRET=your-super-secret-jwt-key-at-least-32-characters-long-for-security
JWT_EXPIRATION=86400000

# Google OAuth
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com

# Email Service
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-specific-password

# WebSocket (Optional)
WEBSOCKET_ALLOWED_ORIGINS=https://your-frontend-domain.com
```

## Setup Instructions

### 1. MongoDB Atlas Setup
1. Create a MongoDB Atlas account
2. Create a new cluster
3. Create a database user
4. Get the connection string
5. Add the connection string to Railway environment variables as `MONGODB_URI`

### 2. Railway Setup
1. Create a Railway account
2. Create a new project
3. Connect your GitHub repository
4. Set environment variables in Railway dashboard
5. Get your Railway token and service ID
6. Add them as GitHub secrets

### 3. GitHub Actions Setup
1. The workflow file is already configured in `.github/workflows/deploy.yml`
2. Add required secrets to your GitHub repository:
   - Go to Settings > Secrets and variables > Actions
   - Add `RAILWAY_TOKEN` and `RAILWAY_SERVICE_ID`

## Deployment Process

### Automatic Deployment
1. Push code to `main` branch
2. GitHub Actions automatically:
   - Runs tests
   - Builds the application
   - Deploys to Railway
3. Railway automatically:
   - Builds Docker image
   - Starts the application
   - Makes it available on the assigned URL

### Manual Deployment
If needed, you can manually trigger deployment:
1. Go to GitHub Actions tab
2. Select the workflow
3. Click "Run workflow"

## Monitoring and Troubleshooting

### Check Deployment Status
- **GitHub Actions**: View workflow runs in the Actions tab
- **Railway Dashboard**: Monitor application logs and metrics
- **Application Health**: Check the `/actuator/health` endpoint (if configured)

### Common Issues
1. **Build Failures**: Check test results and build logs in GitHub Actions
2. **Deployment Failures**: Verify Railway token and service ID
3. **Runtime Errors**: Check Railway application logs
4. **Database Connection**: Verify MongoDB Atlas connection string and network access

### Logs Access
- **Build Logs**: GitHub Actions workflow runs
- **Application Logs**: Railway service dashboard
- **Database Logs**: MongoDB Atlas monitoring

## Security Best Practices

### Secrets Management
- ✅ All secrets stored as environment variables
- ✅ No hardcoded credentials in repository
- ✅ Separate test and production configurations
- ✅ JWT secrets are properly secured

### Database Security
- ✅ MongoDB Atlas provides built-in security
- ✅ Connection strings use authentication
- ✅ Network access properly configured

## Performance Considerations
- Maven dependencies are cached in GitHub Actions
- Docker multi-stage build optimizes image size
- Railway provides automatic scaling
- MongoDB Atlas offers managed performance optimization

## Rollback Strategy
If deployment issues occur:
1. Railway automatically keeps previous versions
2. Can rollback through Railway dashboard
3. Can revert Git commits and redeploy
4. Database migrations should be backward compatible

## Support
For deployment issues:
1. Check GitHub Actions logs
2. Review Railway application logs
3. Verify environment variables
4. Check MongoDB Atlas connectivity