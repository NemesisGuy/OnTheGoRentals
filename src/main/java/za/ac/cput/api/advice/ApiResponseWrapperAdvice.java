package za.ac.cput.api.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource; // Import the Resource class
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import za.ac.cput.api.response.ApiResponse;
import za.ac.cput.controllers.FileController; // It's good practice to be specific
import za.ac.cput.utils.SecurityUtils;

/**
 * ApiResponseWrapperAdvice.java
 * A {@link ResponseBodyAdvice} that intercepts and wraps successful responses
 * from controllers in a standard {@link ApiResponse} envelope.
 * It is configured to exclude file streaming endpoints.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Updated: 2024-06-07
 */
@RestControllerAdvice(basePackages = "za.ac.cput.controllers")
public class ApiResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

    private static final Logger log = LoggerFactory.getLogger(ApiResponseWrapperAdvice.class);

    /**
     * Determines if this advice should be applied.
     * It will NOT apply if:
     * 1. The method is in the FileController.
     * 2. The return type is already an ApiResponse.
     * 3. The return type is a Resource (for file streaming).
     *
     * @param returnType    The return type of the controller method.
     * @param converterType The selected HttpMessageConverter.
     * @return {@code true} if the advice should apply, {@code false} otherwise.
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {

        // *** THE FIX: EXCLUDE THE ENTIRE FILECONTROLLER ***
        // This is the simplest and most robust way to prevent wrapping file streams.
        if (returnType.getContainingClass().equals(FileController.class)) {
            log.trace("Skipping ApiResponse wrapping: Method is in FileController.");
            return false;
        }

        // --- Alternative/Additional Checks for robustness ---

        // Exclude if the method's return type is Resource or a subclass of Resource.
        if (Resource.class.isAssignableFrom(returnType.getParameterType())) {
            log.trace("Skipping ApiResponse wrapping: Return type is a Resource.");
            return false;
        }

        // Avoid wrapping if the return type is already ApiResponse
        if (ApiResponse.class.isAssignableFrom(returnType.getParameterType())) {
            log.trace("Skipping ApiResponse wrapping: Return type is already ApiResponse.");
            return false;
        }

        // Avoid wrapping if the return type is ResponseEntity<ApiResponse<...>>
        if (returnType.getGenericParameterType().getTypeName().startsWith("org.springframework.http.ResponseEntity<za.ac.cput.api.response.ApiResponse")) {
            log.trace("Skipping ApiResponse wrapping: Return type is ResponseEntity<ApiResponse>.");
            return false;
        }

        log.trace("Applying ApiResponse wrapping for return type: {}", returnType.getParameterType().getName());
        return true;
    }

    /**
     * Modifies the response body before it's written. This method will not be called
     * for responses where supports() returns false (e.g., from FileController).
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

        String requesterId = SecurityUtils.getRequesterIdentifier();

        // This check is a safeguard, but supports() should have already filtered out this case.
        if (body instanceof ApiResponse) {
            log.debug("Requester [{}]: Body is already an ApiResponse. Returning as is for path: {}", requesterId, request.getURI().getPath());
            return body;
        }

        log.debug("Requester [{}]: Wrapping successful response body in ApiResponse for path: {}. Body type: {}",
                requesterId, request.getURI().getPath(), body != null ? body.getClass().getName() : "null");

        return new ApiResponse<>(body);
    }
}