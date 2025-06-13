
# How to Use Swagger UI in Your Spring Boot Application

Based on the annotations added to the controllers, you now have a documented API using OpenAPI (formerly Swagger). Here's how to use it:

## Accessing Swagger UI

1.  **Start your Spring Boot application** by running it locally.
2.  **Open your browser** and navigate to:
    ```
    http://localhost:8087/swagger-ui/index.html
    ```
    (Assuming your application runs on port 8087, as configured in `application.properties` (`server.port=8087`)).

## What You Can Do With Swagger UI

### 1. Explore Your API Documentation
*   See a complete list of all API endpoints organized by controller tags (e.g., "Authentication", "Admin - User Management").
*   View detailed information about each endpoint including:
    *   HTTP method (GET, POST, PUT, DELETE)
    *   URL path
    *   Summary and detailed description of the endpoint's purpose.
    *   Request parameters (path variables, query parameters).
    *   Request body schema (for POST/PUT requests).
    *   Response codes and their corresponding schemas (what data to expect on success or error).
    *   Authentication requirements (e.g., if a JWT bearer token is needed).

### 2. Test API Endpoints
*   Click on any endpoint to expand its details.
*   Click the "Try it out" button.
*   Fill in any required parameters (e.g., UUIDs in the path) or provide a JSON request body.
    *   Swagger UI often provides example schemas for request bodies.
*   Click "Execute" to make a live API call to your running application.
*   View the actual HTTP response code, body, and headers directly in the UI.

### 3. Authentication
*   For secured endpoints (those marked with a lock icon or defined with `@SecurityRequirement(name = "bearerAuth")`):
    *   Click the "Authorize" button, usually located at the top right of the Swagger UI page.
    *   A dialog will appear. In the `bearerAuth` (or similarly named JWT security scheme) section, enter your JWT access token in the format: `Bearer <your_actual_token>`.
        *   **Important**: You must include the "Bearer " prefix before the token.
    *   Click "Authorize" in the dialog, then "Close".
    *   All subsequent requests made from Swagger UI for secured endpoints will now include this token in the `Authorization` header.

## Example Workflow for Testing Secured Endpoints

1.  **Register/Login First**: Use the "Authentication" group's `/api/v1/auth/login` (or `/api/v1/auth/register`) endpoint to obtain a JWT access token.
2.  **Copy Token**: From the response body of the login/register request, copy the `accessToken` value.
3.  **Authorize**: Click the global "Authorize" button on the Swagger UI page. Paste the copied token into the `bearerAuth` input field, ensuring it's prefixed with `Bearer `.
4.  **Test Protected Endpoints**: Now you can successfully execute endpoints that require authentication (e.g., `/api/v1/users/me/profile` or admin endpoints).

## Benefits

*   **Interactive Documentation**: Developers (frontend and backend) can understand and explore the API without needing to read through controller code.
*   **Live Testing Tool**: Quickly test API endpoints with various inputs and see real responses, aiding in development and debugging.
*   **Client Generation**: The underlying OpenAPI specification (usually accessible at `/v3/api-docs`) can be used to generate client SDKs in various languages.
*   **Standardized Format**: Documentation follows the OpenAPI Specification, a widely adopted standard.

## Annotation Versions Note

This project uses a mix of Swagger/OpenAPI annotation versions:
*   **`io.swagger.annotations` (Swagger 2 / OpenAPI 2)**: Some older or less recently modified controllers might use these (e.g., `@Api`, `@ApiOperation`, `@ApiParam`).
*   **`io.swagger.v3.oas.annotations` (OpenAPI 3)**: Newer or recently updated controllers predominantly use these (e.g., `@Tag`, `@Operation`, `@Parameter`, `@Schema`).

Swagger UI is generally good at rendering documentation from both sets of annotations. The configuration in `OpenApiConfig.java` ensures that these are processed to generate the OpenAPI v3 specification.

## Keeping Documentation Current

*   The Swagger UI documentation is generated dynamically from the annotations in your controller classes.
*   As you add new endpoints or modify existing ones (e.g., change request parameters, DTOs, or response structures), ensure you update or add the corresponding OpenAPI annotations in the Java code. This will automatically keep the Swagger UI documentation up-to-date.