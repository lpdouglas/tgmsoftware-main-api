# MongoDB SSL Connection Debugging Guide for Render Deployment

## Problem
When accessing `/users` endpoint on Render, getting SSL error:
```
javax.net.ssl.SSLException: (internal_error) Received fatal alert: internal_error
```

## Root Cause Analysis

The error indicates that either:
1. **Environment variables are NOT being set on Render** - causing MongoDB credentials to be empty or invalid
2. **Credentials contain special characters** that need URL encoding
3. **SSL/TLS handshake is failing** due to certificate validation issues

## Things to Check on Render Dashboard

### 1. Environment Variables
Go to your Render service dashboard and verify these environment variables are set:
- `MONGODB_USERNAME` - MongoDB Atlas username with read/write access
- `MONGODB_PASSWORD` - MongoDB Atlas password
- `STRIPE_SECRET_KEY` (or `STRIPE_KEY`) - Stripe secret key

**CRITICAL:** The values must NOT contain unresolved template syntax like `${...}`

### 2. Check the Logs for Debug Output
The application now logs detailed configuration info when it starts. Look for:
- "CONFIGURATION DEBUG INFO" section 
- "MONGODB CONFIGURATION DETAILED DEBUG" section
- Lines like:
  - `Username set: true/false`
  - `Password set: true/false`
  - `URI variables are resolved` (good) vs `contains unresolved variables` (bad)

If you see:
```
! CRITICAL: MongoDB URI contains unresolved variables!
Raw URI: mongodb+srv://${MONGODB_USERNAME}:${MONGODB_PASSWORD}@...
```
This means **the environment variables are NOT set on Render**.

### 3. Test the Connection
When you access `/users`, watch for these logs:
- `=== GET /users endpoint called ===`
- `Attempting to connect to MongoDB...`
- Either "Successfully retrieved X users" or error details

### 4. Special Characters in Passwords
If your MongoDB password contains special characters like:
- `@` → should be `%40`
- `:` → should be `%3A`
- `/` → should be `%2F`
- `?` → should be `%3F`
- `#` → should be `%23`

You may need to URL-encode them when setting the environment variable.

## Quick Fix Checklist

1. **Verify Environment Variables on Render:**
   - Go to Render Dashboard → Your Service → Environment
   - Confirm `MONGODB_USERNAME` and `MONGODB_PASSWORD` are there
   - Copy values and test connectivity locally

2. **Check MongoDB Atlas Credentials:**
   - Log in to MongoDB Atlas
   - Go to Database Access → Your user
   - Verify username and password are correct
   - Make sure the user has access to the database

3. **Verify IP Allowlist:**
   - Go to MongoDB Atlas → Security → Network Access
   - Make sure Render's IP is whitelisted (usually need to whitelist 0.0.0.0/0 for Render)

4. **Enable Debug Logging:**
   - Application already has `org.mongodb.driver: DEBUG` configured
   - Check logs on Render for detailed MongoDB driver messages

5. **Test Connection String Locally:**
   - Copy the exact connection string from the logs
   - Test with MongoDB CLI: `mongo "your-connection-string"`
   - Or use MongoDB Atlas UI connection tester

## Useful Render Build Settings

If using `.env` file locally but need environment variables on Render:
1. Do NOT commit `.env` file to repo
2. Go to Render Dashboard → Service → Environment
3. Manually add each variable
4. Deploy or redeploy the service

## Expected Versus Actual Output

### After Deployment, Logs Should Show:
```
INFO dev.tgmsoft.MainApiApplication - ========== CONFIGURATION DEBUG INFO ==========
INFO dev.tgmsoft.MainApiApplication - Raw Environment Variables:
INFO dev.tgmsoft.MainApiApplication -   MONGODB_USERNAME: SET (length: XX)
INFO dev.tgmsoft.MainApiApplication -   MONGODB_PASSWORD: SET (length: XX)
INFO dev.tgmsoft.MainApiApplication -   MongoDB URI: mongodb+srv://[username]:[PASSWORD]@ac-xxx.mongodb.net...
```

### When Calling /users:
```
INFO dev.tgmsoft.controller.UserController - === GET /users endpoint called ===
INFO dev.tgmsoft.controller.UserController - Attempting to connect to MongoDB...
INFO dev.tgmsoft.controller.UserController - Successfully retrieved N users from database
```

### If There's an Error:
```
ERROR dev.tgmsoft.controller.UserController - ERROR connecting to MongoDB during /users request
ERROR dev.tgmsoft.controller.UserController - Exception type: com.mongodb.MongoSocketWriteException
ERROR dev.tgmsoft.controller.UserController - Exception message: (various)
ERROR dev.tgmsoft.controller.UserController - Root cause: javax.net.ssl.SSLException - ...
```

## Next Steps

1. Deploy the updated code with enhanced logging
2. Check the startup logs for "CONFIGURATION DEBUG INFO" output
3. Access `/users` endpoint and check the logs
4. Share the relevant log sections to diagnose the actual issue
