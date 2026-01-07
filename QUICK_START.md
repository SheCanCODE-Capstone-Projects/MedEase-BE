# Quick Start Guide - MedEase CI/CD Setup

This is a quick reference guide for setting up CI/CD and deploying to Railway.

## 🚀 Quick Setup Checklist

### 1. MongoDB Atlas Setup (5 minutes)
- [ ] Create MongoDB Atlas account
- [ ] Create cluster (free M0 tier works)
- [ ] Create database user
- [ ] Allow network access (0.0.0.0/0 for Railway)
- [ ] Copy connection string: `mongodb+srv://user:pass@cluster.mongodb.net/medease?retryWrites=true&w=majority`

### 2. Google OAuth Setup (5 minutes)
- [ ] Go to [Google Cloud Console](https://console.cloud.google.com)
- [ ] Create OAuth 2.0 credentials
- [ ] Copy Client ID

### 3. Gmail App Password (2 minutes)
- [ ] Enable 2FA on Gmail
- [ ] Generate app password at [myaccount.google.com/apppasswords](https://myaccount.google.com/apppasswords)
- [ ] Copy 16-character password

### 4. Railway Setup (5 minutes)
- [ ] Sign up at [railway.app](https://railway.app)
- [ ] Create new project from GitHub repo
- [ ] Get Railway token (Account Settings → Tokens)
- [ ] Get Service ID (from service settings)

### 5. GitHub Secrets (3 minutes)
Go to: Repository → Settings → Secrets and variables → Actions

Add these secrets:
- `RAILWAY_TOKEN` - Your Railway token
- `RAILWAY_SERVICE_ID` - Your Railway service ID
- `JWT_SECRET` - Generate with: `openssl rand -base64 32`
- `JWT_EXPIRATION` - `86400000`
- `DB_URI` - Your MongoDB connection string
- `GOOGLE_CLIENT_ID` - Your Google Client ID
- `MAIL_USERNAME` - Your Gmail address
- `MAIL_PASSWORD` - Your Gmail app password

### 6. Railway Environment Variables
In Railway dashboard → Your Service → Variables, add:

```bash
JWT_SECRET=<same-as-github-secret>
JWT_EXPIRATION=86400000
DB_URI=<your-mongodb-connection-string>
GOOGLE_CLIENT_ID=<your-google-client-id>
MAIL_USERNAME=<your-gmail>
MAIL_PASSWORD=<your-app-password>
WEBSOCKET_ALLOWED_ORIGINS=https://your-frontend.com
```

**Note:** Don't set `PORT` - Railway sets it automatically!

### 7. Deploy! 🎉
```bash
git add .
git commit -m "Setup CI/CD pipeline"
git push origin main
```

Watch the deployment:
- GitHub Actions: Repository → Actions tab
- Railway: Dashboard → Deployments

## 🔍 Verify Deployment

1. **Check Railway Logs**
   - Railway Dashboard → Service → Deployments → View Logs
   - Look for: "Started MedEaseApplication"

2. **Test API**
   - Get your Railway URL (Service → Settings → Domains)
   - Test: `https://your-app.railway.app/swagger-ui.html`
   - Test: `https://your-app.railway.app/api/auth/register`

## 📝 Environment Variables Summary

| Variable | Where to Set | Example |
|----------|--------------|---------|
| `JWT_SECRET` | GitHub Secrets + Railway | `openssl rand -base64 32` |
| `JWT_EXPIRATION` | GitHub Secrets + Railway | `86400000` |
| `DB_URI` | GitHub Secrets + Railway | `mongodb+srv://...` |
| `GOOGLE_CLIENT_ID` | GitHub Secrets + Railway | `123.apps.googleusercontent.com` |
| `MAIL_USERNAME` | GitHub Secrets + Railway | `your@gmail.com` |
| `MAIL_PASSWORD` | GitHub Secrets + Railway | `abcd efgh ijkl mnop` |
| `WEBSOCKET_ALLOWED_ORIGINS` | Railway only | `https://frontend.com` |
| `PORT` | Railway (automatic) | Don't set manually |

## ⚠️ Common Issues

**Application won't start:**
- Check all environment variables are set in Railway
- Verify MongoDB connection string is correct
- Check Railway logs for errors

**Database connection failed:**
- Verify MongoDB Atlas network access allows all IPs (0.0.0.0/0)
- Check username/password in connection string

**CI/CD fails:**
- Verify all GitHub secrets are set
- Check Railway token is valid
- Ensure Service ID is correct

## 📚 Full Documentation

See [DEPLOYMENT.md](./DEPLOYMENT.md) for detailed instructions.

