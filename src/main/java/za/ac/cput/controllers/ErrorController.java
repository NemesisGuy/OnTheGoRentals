package za.ac.cput.controllers;
/**
 * ErrorController.java
 * This is the controller for the error page
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 */

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ErrorController {

    @RequestMapping(value = "/error", produces = "text/html")
    public ModelAndView error(HttpServletRequest request, HttpServletResponse response, Exception exception) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", exception);
        mav.addObject("url", request.getRequestURL());
        mav.setViewName("error");
        return mav;
    }
}
