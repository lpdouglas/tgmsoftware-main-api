# MongoDB SSL Connection Debugging Guide for Render Deployment

## ✅ ROOT CAUSE IDENTIFIED

**The application is trying to connect to `localhost:27017` instead of MongoDB Atlas.**

This means **`MONGODB_USERNAME` and `MONGODB_PASSWORD` environment variables are NOT set on Render.**

## The Problem

When logs show:
```
❌ CRITICAL: MongoDB URI is using LOCALHOST!
   URI: mongodb+srv://${MONGODB_USERNAME}:${MONGODB_PASSWORD}@localhost:27017
   This means environment variables are NOT being interpolated!
```

Or in old logs:
```
[{address=localhost:27017, type=UNKNOWN, state=CONNECTING, exception={...}}]
```

This confirms: **Environment variables are missing on Render Host.**

## Solution: Add Environment Variables to Render

### Step 1: Go to Render Dashboard
1. Login to [Render.com](https://render.com)
2. Find your service (main-api)
3. Click **Settings** (or scroll down on service page)
4. Find **Environment** section

### Step 2: Add These Variables
Set EXACTLY these environment variables:

```
MONGODB_USERNAME=<your-mongodb-atlas-username>
MONGODB_PASSWORD=<your-mongodb-atlas-password>
STRIPE_SECRET_KEY=<your-stripe-secret-key>
```

⚠️ **CRITICAL POINTS:**
- Do NOT include the `${...}` syntax - put the actual values
- Values must match exactly what you use to connect in MongoDB Atlas
- If your password has special characters, it might need URL encoding

### Step 3: Deploy
After adding environment variables:
1. Go back to your service
2. Click **Deploy** or **Redeploy**
3. Wait for deployment to complete

### Step 4: Check the Logs
After deployment, look for these lines:

#### ✅ If Variables Are Set:
```
Raw Environment Variables:
  MONGODB_USERNAME: SET (length: XX)
  MONGODB_PASSWORD: SET (length: XX)

⚠️ ACTUAL MongoDB Connection URI Being Used:
✓ URI looks correct: mongodb+srv://[username]:[PASSWORD]@ac-6ttpd7u-shard-00-*.kbnb6qc.mongodb.net...
```

#### ❌ If Variables Are Missing:
```
Raw Environment Variables:
  MONGODB_USERNAME: ❌ NOT SET OR EMPTY
  MONGODB_PASSWORD: ❌ NOT SET OR EMPTY

⚠️ ACTUAL MongoDB Connection URI Being Used:
❌ CRITICAL: MongoDB URI is using LOCALHOST!
   URI: mongodb+srv://${MONGODB_USERNAME}:${MONGODB_PASSWORD}@localhost:27017
   This means environment variables are NOT being interpolated!
```

If you see this, **go back to Step 1-3 and add the environment variables.**

## How to Find Your MongoDB Atlas Credentials

1. Login to [MongoDB Atlas](https://cloud.mongodb.com)
2. Go to **Database Access** (left sidebar)
3. Find your database user
4. Note the **Username**
5. For the password, you need to either:
   - Know it from when you created the user
   - Reset it: Click the user → Edit → Change Password

Your connection string template is in:
- **Databases** → **Connect** → **Copy connection string** → **Python** option
- Format: `mongodb+srv://USERNAME:PASSWORD@ac-6ttpd7u...`

## Common Issues

### Issue: Password Still Doesn't Work
**Possible causes:**
1. Character was copied incorrectly (extra spaces?)
2. Password contains special characters needing encoding:
   - `@` → `%40`
   - `:` → `%3A`
   - `/` → `%2F`
   - `#` → `%23`

**Solution:** Try URL-encoding the password before setting it on Render

### Issue: Variables Are Set But Still Getting localhost
1. Clear Render's build cache: Settings → Clear build cache → Deploy
2. Or redeploy from a new commit

### Issue: Connection Timeout (not localhost error)
- If you see `timeout` or `SSL alert: internal_error`, it's a different issue
- Check MongoDB Atlas **Network Access** - make sure Render's IP is whitelisted
- Or whitelist 0.0.0.0/0 (all IPs)

## Verification Checklist

Before redeploying, verify in order:

- [ ] Opened Render Dashboard
- [ ] Opened your service Settings
- [ ] Scrolled to Environment section
- [ ] Added `MONGODB_USERNAME` (the actual username, not a variable)
- [ ] Added `MONGODB_PASSWORD` (the actual password, not a variable)
- [ ] Clicked "Save"
- [ ] Clicked "Deploy" or "Redeploy"
- [ ] Waited for deployment to finish
- [ ] Checked logs for "SET" confirmation messages
- [ ] Accessed `/users` endpoint
- [ ] Logs now show correct MongoDB host (not localhost)

## Expected Output When Working

### On Application Startup:
```
INFO 1 --- [main-api] dev.tgmsoft.MainApiApplication : ========== APPLICATION STARTUP ==========
INFO 1 --- [main-api] dev.tgmsoft.MainApiApplication : ========== CONFIGURATION DEBUG INFO ==========
INFO 1 --- [main-api] dev.tgmsoft.MainApiApplication : Raw Environment Variables:
INFO 1 --- [main-api] dev.tgmsoft.MainApiApplication :   MONGODB_USERNAME: SET (length: 12)
INFO 1 --- [main-api] dev.tgmsoft.MainApiApplication :   MONGODB_PASSWORD: SET (length: 24)
INFO 1 --- [main-api] dev.tgmsoft.MainApiApplication : 
INFO 1 --- [main-api] dev.tgmsoft.MainApiApplication : ⚠️ ACTUAL MongoDB Connection URI Being Used:
INFO 1 --- [main-api] dev.tgmsoft.MainApiApplication : ✓ URI looks correct: mongodb+srv://[username]:[PASSWORD]@ac-6ttpd7u-shard-00-00.kbnb6qc.mongodb.net...
```

### When Calling /users:
```
INFO 1 --- [main-api] dev.tgmsoft.controller.UserController : === GET /users endpoint called ===
INFO 1 --- [main-api] dev.tgmsoft.controller.UserController : Attempting to connect to MongoDB...
INFO 1 --- [main-api] dev.tgmsoft.controller.UserController : Successfully retrieved 5 users from database
```

## Quick Reference

| Issue | What You'll See | Next Step |
|-------|-----------------|-----------|
| Variables not set | `NOT SET OR EMPTY` | Add vars to Render Environment |
| Variables set but using localhost | `using LOCALHOST` | Clear cache and redeploy |
| Variables set + correct host | `URI looks correct` | Should work now - test `/users` |

## Need More Help?

Share these logs with your team:
1. The startup "CONFIGURATION DEBUG INFO" section
2. Any error message when calling `/users`
3. Screenshot of your Render Environment variables (password masked)

This will help pinpoint exactly what's wrong.

