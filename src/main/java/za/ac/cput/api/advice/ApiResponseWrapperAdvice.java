package za.ac.cput.api.advice; // Ensure this package is scanned by Spring

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import za.ac.cput.api.response.ApiResponse; // Your wrapper DTO

@RestControllerAdvice(basePackages = "za.ac.cput.controllers") // IMPORTANT: Adjust to your controllers' package(s)
public class ApiResponseWrapperAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // This advice should apply to any return type that is NOT already an ApiResponse
        // and also avoid wrapping Spring Boot Actuator endpoints or Springdoc/Swagger UI if they are under your basePackages.
        // You might need to add more conditions here if you have specific controllers/endpoints to exclude.
        // Example: Exclude if controller is annotated with a custom @NoApiResponseWrapper
        // Example: Exclude if return type is ResponseEntity and its body is already ApiResponse

        // Check if the method is in a controller you want to wrap.
        // The basePackages in @RestControllerAdvice already handles this.

        // Avoid wrapping if the return type is already ApiResponse
        if (returnType.getParameterType().equals(ApiResponse.class)) {
            return false;
        }
        // Avoid wrapping if the return type is ResponseEntity<ApiResponse<...>>
        if (returnType.getGenericParameterType().getTypeName().startsWith("org.springframework.http.ResponseEntity<za.ac.cput.api.response.ApiResponse<")) {
            return false;
        }

        // You could also add checks here for specific annotations on methods/controllers if you want to exclude some
        // For example, if a method is annotated with @RawResponse, don't wrap it.
        // if (returnType.hasMethodAnnotation(RawResponse.class)) {
        //     return false;
        // }

        return true; // Apply to all other controller methods within the basePackages
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        // If the body is already an instance of ApiResponse (e.g., returned by GlobalExceptionHandler
        // or if a controller method explicitly returned ApiResponse), don't wrap it again.
        if (body instanceof ApiResponse) {
            return body;
        }

        // If the controller returned ResponseEntity<YourDTO>, the 'body' here will be YourDTO.
        // If the controller returned YourDTO directly, 'body' will also be YourDTO.
        // Wrap the successful body in your ApiResponse structure.
        return new ApiResponse<>(body); // Assumes your ApiResponse constructor for success takes the data
    }
}