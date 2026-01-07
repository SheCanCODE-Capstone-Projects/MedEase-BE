# Quick Railway Setup Guide

This is a quick reference guide for setting up Railway deployment. For detailed instructions, see [DEPLOYMENT.md](./DEPLOYMENT.md).

## Quick Start (5 minutes)

### 1. Railway Setup

1. **Sign up** at [railway.app](https://railway.app)
2. **Create new project** → **Deploy from GitHub repo**
3. **Select your repository** (`MedEase-BE`)
4. Railway will auto-detect Java/Maven

### 2. MongoDB Atlas Setup

1. **Sign up** at [mongodb.com/cloud/atlas](https://www.mongodb.com/cloud/atlas)
2. **Create free cluster** (M0)
3. **Database Access**: Create user with password
4. **Network Access**: Allow from anywhere (0.0.0.0/0)
5. **Get connection string**: 
   ```
   mongodb+srv://username:password@cluster.mongodb.net/medease?retryWrites=true&w=majority
   ```

### 3. Environment Variables in Railway

Go to Railway project → **Variables** tab → Add these:

```bash
JWT_SECRET=<generate-a-random-32-char-string>
JWT_EXPIRATION=86400000
DB_URI=<your-mongodb-atlas-connection-string>
GOOGLE_CLIENT_ID=<your-google-oauth-client-id>
MAIL_USERNAME=<your-gmail@gmail.com>
MAIL_PASSWORD=<gmail-app-password>
WEBSOCKET_ALLOWED_ORIGINS=https://your-frontend.vercel.app,http://localhost:3000
```

### 4. Generate JWT Secret

```bash
# On Linux/Mac
openssl rand -base64 32

# Or use online generator
# https://www.grc.com/passwords.htm (64 characters)
```

### 5. Gmail App Password

1. Enable 2FA on Google Account
2. Go to [App Passwords](https://myaccount.google.com/apppasswords)
3. Generate password for "MedEase Backend"
4. Use as `MAIL_PASSWORD`

### 6. Deploy

Railway will automatically:
- Build your application
- Deploy it
- Provide a public URL

Check **Deployments** tab for status.

---

## CI/CD Setup (GitHub Actions)

### 1. Get Railway Token

1. Railway → **Account Settings** → **API**
2. **New Token** → Copy token

### 2. Get Project ID

1. Railway project → **Settings**
2. Copy **Project ID**

### 3. Add GitHub Secrets

1. GitHub repo → **Settings** → **Secrets and variables** → **Actions**
2. Add secrets:
   - `RAILWAY_TOKEN` = Your Railway API token
   - `RAILWAY_PROJECT_ID` = Your Railway project ID

### 4. Test CI/CD

1. Push to `main` branch
2. Check **Actions** tab
3. Watch it build and deploy!

---

## Verify Deployment

1. **Check Railway URL**: `https://your-app.railway.app`
2. **Swagger UI**: `https://your-app.railway.app/swagger-ui.html`
3. **Test endpoint**:
   ```bash
   curl https://your-app.railway.app/api/auth/register
   ```

---

## Common Issues

### Build Fails
- Check Railway logs
- Verify Maven wrapper: `chmod +x mvnw` (if needed)

### Can't Connect to Database
- Check MongoDB Atlas network access
- Verify connection string format
- Check database user credentials

### Email Not Working
- Verify Gmail app password (not regular password)
- Check 2FA is enabled
- Verify `MAIL_USERNAME` and `MAIL_PASSWORD`

### JWT Errors
- Ensure `JWT_SECRET` is at least 32 characters
- Check it's set in Railway variables

---

## Next Steps

1. ✅ Deploy to Railway
2. ✅ Set up MongoDB Atlas
3. ✅ Configure environment variables
4. ✅ Set up CI/CD
5. ✅ Test API endpoints
6. ✅ Update frontend with Railway URL

For detailed instructions, see [DEPLOYMENT.md](./DEPLOYMENT.md).

