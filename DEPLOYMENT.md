# MedEase Backend - Deployment Guide

This guide covers deploying the MedEase backend to Railway with MongoDB Atlas, including CI/CD setup with GitHub Actions.

## Prerequisites

- GitHub account with repository access
- Railway account (sign up at [railway.app](https://railway.app))
- MongoDB Atlas account (sign up at [mongodb.com/cloud/atlas](https://www.mongodb.com/cloud/atlas))
- Google Cloud Console account (for OAuth)
- Gmail account with App Password enabled (for email functionality)

---

## Step 1: Set Up MongoDB Atlas

1. **Create a MongoDB Atlas Account**
   - Go to [mongodb.com/cloud/atlas](https://www.mongodb.com/cloud/atlas)
   - Sign up or log in

2. **Create a New Cluster**
   - Click "Build a Database"
   - Choose a free tier (M0) or paid tier
   - Select a cloud provider and region
   - Name your cluster (e.g., "MedEase-Cluster")

3. **Configure Database Access**
   - Go to "Database Access" → "Add New Database User"
   - Create a user with username and password
   - Set privileges to "Atlas admin" or "Read and write to any database"
   - Save the username and password securely

4. **Configure Network Access**
   - Go to "Network Access" → "Add IP Address"
   - Click "Allow Access from Anywhere" (0.0.0.0/0) for Railway deployment
   - Or add Railway's IP ranges if you prefer more security

5. **Get Connection String**
   - Go to "Database" → "Connect" → "Connect your application"
   - Copy the connection string
   - Replace `<password>` with your database user password
   - Replace `<dbname>` with your database name (e.g., "medease")
   - Format: `mongodb+srv://username:password@cluster.mongodb.net/medease?retryWrites=true&w=majority`

---

## Step 2: Set Up Google OAuth

1. **Create Google Cloud Project**
   - Go to [console.cloud.google.com](https://console.cloud.google.com)
   - Create a new project or select existing one

2. **Enable Google+ API**
   - Go to "APIs & Services" → "Library"
   - Search for "Google+ API" and enable it

3. **Create OAuth 2.0 Credentials**
   - Go to "APIs & Services" → "Credentials"
   - Click "Create Credentials" → "OAuth client ID"
   - Choose "Web application"
   - Add authorized JavaScript origins:
     - `http://localhost:3000` (for local development)
     - `https://your-frontend-domain.com` (for production)
   - Add authorized redirect URIs:
     - `http://localhost:3000/auth/callback` (for local)
     - `https://your-frontend-domain.com/auth/callback` (for production)
   - Copy the Client ID

---

## Step 3: Set Up Gmail App Password

1. **Enable 2-Factor Authentication**
   - Go to [myaccount.google.com/security](https://myaccount.google.com/security)
   - Enable 2-Step Verification

2. **Generate App Password**
   - Go to [myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords)
   - Select "Mail" and "Other (Custom name)"
   - Enter "MedEase Backend"
   - Copy the 16-character password (no spaces)

---

## Step 4: Set Up Railway

1. **Create Railway Account**
   - Go to [railway.app](https://railway.app)
   - Sign up with GitHub

2. **Create New Project**
   - Click "New Project"
   - Select "Deploy from GitHub repo"
   - Connect your GitHub account and select the MedEase repository

3. **Configure Service**
   - Railway will detect the Dockerfile automatically
   - The service will be created but not deployed yet

4. **Get Railway Token (for CI/CD)**
   - Go to Railway Dashboard → Account Settings → Tokens
   - Click "New Token"
   - Name it "GitHub Actions Deploy"
   - Copy the token (you'll need this for GitHub Secrets)

5. **Get Service ID**
   - In your Railway project, click on your service
   - The Service ID is in the URL or settings
   - Copy it for GitHub Secrets

---

## Step 5: Configure Environment Variables in Railway

In your Railway project, go to your service → Variables tab and add:

```bash
# JWT Configuration
JWT_SECRET=<generate-a-random-32+character-string>
JWT_EXPIRATION=86400000

# MongoDB Atlas
DB_URI=mongodb+srv://username:password@cluster.mongodb.net/medease?retryWrites=true&w=majority

# Google OAuth
GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com

# Email Configuration
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-16-char-app-password

# WebSocket Origins (comma-separated, no spaces)
WEBSOCKET_ALLOWED_ORIGINS=https://your-frontend-domain.com,https://www.your-frontend-domain.com
```

**Important Notes:**
- `JWT_SECRET`: Generate a secure random string (at least 32 characters)
  ```bash
  # Generate on Linux/Mac:
  openssl rand -base64 32
  
  # Or use an online generator
  ```
- `PORT`: Railway automatically sets this, don't override it
- `DB_URI`: Use your MongoDB Atlas connection string
- `WEBSOCKET_ALLOWED_ORIGINS`: No spaces between URLs, comma-separated

---

## Step 6: Set Up GitHub Secrets for CI/CD

1. **Go to GitHub Repository**
   - Navigate to your repository on GitHub
   - Go to Settings → Secrets and variables → Actions

2. **Add the Following Secrets:**

   | Secret Name | Value | Description |
   |------------|-------|-------------|
   | `RAILWAY_TOKEN` | Your Railway token | From Step 4.4 |
   | `RAILWAY_SERVICE_ID` | Your service ID | From Step 4.5 |
   | `JWT_SECRET` | Your JWT secret | Same as Railway |
   | `JWT_EXPIRATION` | 86400000 | JWT expiration in ms |
   | `DB_URI` | MongoDB connection string | For testing |
   | `GOOGLE_CLIENT_ID` | Your Google Client ID | For testing |
   | `MAIL_USERNAME` | Your Gmail address | For testing |
   | `MAIL_PASSWORD` | Your app password | For testing |

3. **Add Secrets:**
   - Click "New repository secret"
   - Enter name and value
   - Click "Add secret"

---

## Step 7: Deploy

### Option A: Automatic Deployment (CI/CD)

1. **Push to Main Branch**
   ```bash
   git add .
   git commit -m "Setup CI/CD pipeline"
   git push origin main
   ```

2. **Monitor Deployment**
   - Go to GitHub → Actions tab
   - Watch the workflow run
   - Check Railway dashboard for deployment status

### Option B: Manual Deployment

1. **Deploy via Railway Dashboard**
   - Go to Railway project
   - Click "Deploy" or trigger a new deployment

2. **Or use Railway CLI**
   ```bash
   npm install -g @railway/cli
   railway login
   railway link
   railway up
   ```

---

## Step 8: Verify Deployment

1. **Check Railway Logs**
   - Go to Railway dashboard → Your service → Deployments
   - Click on the latest deployment → View logs
   - Look for "Started MedEaseApplication" message

2. **Test API Endpoints**
   - Get your Railway URL from the service settings
   - Test health endpoint: `https://your-app.railway.app/actuator/health`
   - Test Swagger UI: `https://your-app.railway.app/swagger-ui.html`

3. **Test Authentication**
   - Try registering a new patient
   - Test login endpoint
   - Verify JWT token generation

---

## Troubleshooting

### Common Issues

1. **Application Won't Start**
   - Check Railway logs for errors
   - Verify all environment variables are set
   - Ensure MongoDB connection string is correct
   - Check that PORT is not manually set (Railway sets it)

2. **Database Connection Failed**
   - Verify MongoDB Atlas network access allows Railway IPs
   - Check database credentials in connection string
   - Ensure database name exists in Atlas

3. **JWT Errors**
   - Verify JWT_SECRET is at least 32 characters
   - Check JWT_EXPIRATION is a valid number

4. **Email Not Sending**
   - Verify Gmail app password is correct (16 chars, no spaces)
   - Check MAIL_USERNAME is correct Gmail address
   - Ensure 2FA is enabled on Gmail account

5. **CI/CD Pipeline Fails**
   - Check GitHub Actions logs
   - Verify all secrets are set correctly
   - Ensure Railway token is valid

### Viewing Logs

**Railway:**
```bash
railway logs
```

**GitHub Actions:**
- Go to repository → Actions tab → Click on workflow run

---

## Environment Variables Reference

| Variable | Required | Description | Example |
|----------|----------|-------------|---------|
| `JWT_SECRET` | Yes | Secret key for JWT (min 32 chars) | `my-super-secret-key-32-chars-min` |
| `JWT_EXPIRATION` | No | JWT expiration in milliseconds | `86400000` (24 hours) |
| `DB_URI` | Yes | MongoDB Atlas connection string | `mongodb+srv://user:pass@cluster.mongodb.net/db` |
| `GOOGLE_CLIENT_ID` | Yes | Google OAuth client ID | `123456.apps.googleusercontent.com` |
| `MAIL_USERNAME` | Yes | Gmail address for sending emails | `your-email@gmail.com` |
| `MAIL_PASSWORD` | Yes | Gmail app-specific password | `abcd efgh ijkl mnop` |
| `PORT` | No | Server port (Railway sets automatically) | `8091` |
| `WEBSOCKET_ALLOWED_ORIGINS` | No | Comma-separated allowed origins | `https://example.com,https://www.example.com` |

---

## Security Best Practices

1. **Never commit secrets to Git**
   - Use environment variables only
   - Keep `.env` in `.gitignore`

2. **Use strong JWT secrets**
   - Minimum 32 characters
   - Use random, unpredictable strings

3. **Restrict MongoDB Network Access**
   - Use Railway IP ranges if possible
   - Regularly rotate database passwords

4. **Enable MongoDB Atlas Encryption**
   - Use TLS/SSL connections
   - Enable encryption at rest

5. **Monitor Deployments**
   - Review Railway logs regularly
   - Set up alerts for failures

---

## Next Steps

- Set up custom domain in Railway
- Configure SSL certificates
- Set up monitoring and alerts
- Configure backup strategy for MongoDB
- Set up staging environment

---

## Support

For issues or questions:
- Check Railway documentation: [docs.railway.app](https://docs.railway.app)
- MongoDB Atlas docs: [docs.atlas.mongodb.com](https://docs.atlas.mongodb.com)
- GitHub Actions docs: [docs.github.com/actions](https://docs.github.com/actions)
