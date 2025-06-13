package za.ac.cput.controllers;
/**
 * ErrorController.java
 * This is the controller for the error page
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * ErrorController.java
 * This controller handles the display of a generic error page.
 * It uses @ControllerAdvice to be globally available for error handling.
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */
@ControllerAdvice
@Api(value = "Error Handling", tags = "Error Handling", description = "Provides a generic error page for unhandled exceptions.")
public class ErrorController {

    /**
     * Handles requests to the /error path and displays a generic error page.
     * This method is typically invoked by the Spring Boot error handling mechanism.
     *
     * @param request The HttpServletRequest containing information about the request that led to the error.
     * @param response The HttpServletResponse.
     * @param exception The exception that occurred.
     * @return A ModelAndView object that renders the "error" view, populated with exception details.
     */
    @RequestMapping(value = "/error", produces = "text/html")
    @ApiOperation(value = "Display Generic Error Page",
            notes = "Renders an HTML error page when an unhandled exception occurs. This is not a typical REST API endpoint.",
            response = ModelAndView.class)
    public ModelAndView error(
            @ApiParam(value = "The HTTP request that resulted in an error.", required = true) HttpServletRequest request,
            @ApiParam(value = "The HTTP response.", required = true) HttpServletResponse response,
            @ApiParam(value = "The exception that was thrown.", required = true) Exception exception) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", exception);
        mav.addObject("url", request.getRequestURL());
        mav.setViewName("error"); // Assumes an "error.html" or similar view template exists
        return mav;
    }
}
