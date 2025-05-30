package za.ac.cput.api.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import za.ac.cput.api.response.ApiResponse;
import za.ac.cput.utils.SecurityUtils; // If you want to log requester for wrapped responses

/**
 * ApiResponseWrapperAdvice.java
 * A {@link ResponseBodyAdvice} that intercepts successful responses from controllers
 * within the specified base packages and wraps them in a standard {@link ApiResponse} envelope.
 * This ensures a consistent JSON structure for all successful API responses.
 *
 * Author: Peter Buckingham (220165289) // Assuming based on context
 * Date: [Date of creation - e.g., 2025-05-28]
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
@RestControllerAdvice(basePackages = "za.ac.cput.controllers") // IMPORTANT: Adjust to your REST controllers' package(s)
public class ApiResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

    private static final Logger log = LoggerFactory.getLogger(ApiResponseWrapperAdvice.class);

    /**
     * Determines if this advice should be applied to the given controller method's return type.
     * It applies if the response is not already an {@link ApiResponse} or a ResponseEntity containing an ApiResponse.
     *
     * @param returnType    The return type of the controller method.
     * @param converterType The selected HttpMessageConverter.
     * @return {@code true} if the advice should apply, {@code false} otherwise.
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Avoid wrapping if the return type is already ApiResponse
        if (ApiResponse.class.isAssignableFrom(returnType.getParameterType())) {
            log.trace("Skipping ApiResponse wrapping: Return type is already ApiResponse.");
            return false;
        }
        // Avoid wrapping if the return type is ResponseEntity<ApiResponse<...>>
        // A more robust check for ResponseEntity<ApiResponse> might involve inspecting generic types carefully.
        // This string check is a common approach but can be brittle.
        if (returnType.getGenericParameterType().getTypeName().startsWith("org.springframework.http.ResponseEntity<za.ac.cput.api.response.ApiResponse")) {
            log.trace("Skipping ApiResponse wrapping: Return type is ResponseEntity<ApiResponse>.");
            return false;
        }

        // Add more specific exclusions if needed, e.g., for actuator endpoints, Swagger UI, etc.
        // if (returnType.getDeclaringClass().getPackage().getName().startsWith("org.springdoc")) {
        //     return false;
        // }

        log.trace("Applying ApiResponse wrapping for return type: {}", returnType.getParameterType().getName());
        return true;
    }

    /**
     * Modifies the response body before it's written by the HttpMessageConverter.
     * Wraps the original body in an {@link ApiResponse} if it's not already one.
     *
     * @param body                  The original response body.
     * @param returnType            The return type of the controller method.
     * @param selectedContentType   The content type selected by the converter.
     * @param selectedConverterType The HttpMessageConverter selected to write the response.
     * @param request               The current ServerHttpRequest.
     * @param response              The current ServerHttpResponse.
     * @return The (potentially wrapped) response body.
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        String requesterId = SecurityUtils.getRequesterIdentifier(); // Get requester for context if needed

        if (body instanceof ApiResponse) {
            // This check is somewhat redundant if supports() already filters out ApiResponse,
            // but acts as a safeguard.
            log.debug("Requester [{}]: Body is already an ApiResponse. Returning as is for path: {}", requesterId, request.getURI().getPath());
            return body;
        }

        // Special handling for cases where Spring might return a String for an error view name
        // or specific error scenarios not caught by ExceptionHandlers that produce structured errors.
        // This is less common if controllers always return ResponseEntity or DTOs for success.
        if (body instanceof String && !(selectedContentType != null && selectedContentType.includes(MediaType.APPLICATION_JSON))) {
            // If it's a String and not explicitly JSON, it might be an error view or plain text.
            // Decide if these should be wrapped or not. For API consistency, even string data could be wrapped.
            log.debug("Requester [{}]: Wrapping String body in ApiResponse for path: {}", requesterId, request.getURI().getPath());
        } else {
            log.debug("Requester [{}]: Wrapping successful response body in ApiResponse for path: {}. Body type: {}",
                    requesterId, request.getURI().getPath(), body != null ? body.getClass().getName() : "null");
        }
        return new ApiResponse<>(body);
    }
}