
# How to Use Swagger UI in Your Spring Boot Application

Based on the annotations we've added to your controllers, you now have a fully documented API using Swagger/OpenAPI. Here's how to use it:

## Accessing Swagger UI

1. **Start your Spring Boot application** by running it locally
2. **Open your browser** and navigate to:
   ```
   http://localhost:8087/swagger-ui/index.html
   ```
   (Assuming your application runs on port 8087)

## What You Can Do With Swagger UI

### 1. Explore Your API Documentation
- See a complete list of all API endpoints organized by controller tags
- View detailed information about each endpoint including:
    - HTTP method (GET, POST, PUT, DELETE)
    - URL path
    - Description
    - Request parameters
    - Request body schema
    - Response codes and schemas
    - Authentication requirements

### 2. Test API Endpoints
- Click on any endpoint to expand it
- Click the "Try it out" button
- Fill in required parameters or request body
- Click "Execute" to make a real API call
- View the response directly in the UI

### 3. Authentication
- For secured endpoints (those with `@SecurityRequirement(name = "bearerAuth")`):
    - Click the "Authorize" button at the top of the page
    - Enter your JWT token (without the "Bearer" prefix)
    - All subsequent requests will include this token

## Example Workflow

1. **Login First**: Use the Auth Controller's `/api/v1/auth/login` endpoint to get a token
2. **Authorize**: Click the "Authorize" button and paste your token
3. **Test Protected Endpoints**: Now you can test endpoints that require authentication

## Benefits

- **Interactive Documentation**: Developers can understand your API without reading code
- **Testing Tool**: Test endpoints without needing Postman or other tools
- **Client Generation**: The OpenAPI specification can be used to generate client libraries
- **Standardized Format**: Documentation follows OpenAPI standards

## Additional Notes

- The Swagger UI is configured in your `OpenApiConfig.java` file
- You can customize the appearance and behavior by modifying this configuration
- The documentation is generated from the annotations we've added to your controllers
- As you add new endpoints or modify existing ones, update the annotations to keep documentation current

Would you like me to explain how to add Swagger annotations to any remaining controllers in your project?