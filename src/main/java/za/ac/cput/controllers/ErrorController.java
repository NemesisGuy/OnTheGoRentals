package za.ac.cput.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * ErrorController.java
 * This controller handles the display of a generic HTML error page for unhandled exceptions
 * that might occur during server-side rendering or for direct browser access to the /error path.
 * <p>
 * Note: For REST API error handling, a dedicated @RestControllerAdvice with @ExceptionHandler
 * methods returning JSON is the preferred approach.
 *
 * @author Peter Buckingham (220165289)
 * @version 2.0
 */
@ControllerAdvice
public class ErrorController {

    /**
     * Handles requests to the /error path and displays a generic HTML error page.
     * This method is typically invoked by the Spring Boot error handling mechanism for non-API requests.
     * <p>
     * This endpoint is hidden from the public OpenAPI documentation as it is not a consumable REST API endpoint.
     *
     * @param request   The HttpServletRequest containing information about the request that led to the error.
     * @param response  The HttpServletResponse.
     * @param exception The exception that occurred, if available.
     * @return A ModelAndView object that renders the "error" view, populated with exception details.
     */
    @RequestMapping(value = "/error", produces = "text/html")
    @Hidden // This annotation cleanly removes the endpoint from the Swagger UI documentation.
    public ModelAndView error(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", exception);
        mav.addObject("url", request.getRequestURL());
        mav.addObject("status", response.getStatus());
        mav.setViewName("error"); // Assumes an "error.html" or similar view template exists
        return mav;
    }
}