package za.ac.cput.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.utils.SecurityUtils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * IndexController.java
 * A simple controller providing a basic greeting message with a visitor counter.
 * This was primarily for testing purposes and might be removed in production.
 * <p>
 * Author: Peter Buckingham (220165289)
 * Date: 05 April 2023
 * Updated by: Peter Buckingham
 * Updated: 2025-05-28
 */
// TODO: Consider removing this class if it was only for testing purposes, as per original TODO.
@RestController
@Api(value = "Index/Greeting", tags = "Index/Greeting", description = "Provides a simple greeting message with a visitor counter.")
public class IndexController {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);
    private static final String TEMPLATE = "Hello, %s!"; // Renamed for clarity, though not used in current output
    private final AtomicLong counter = new AtomicLong();

    /**
     * Handles requests to various root/home paths and returns a greeting message
     * including an incrementing visitor count. This endpoint is publicly accessible.
     *
     * @return A {@link Message} object containing the greeting and visitor count.
     */
    @GetMapping({"/", "/home", "/index", "/api/home", "/api/hello", "/api/index", "/api/greeting"})
    @ApiOperation(value = "Get Greeting Message",
            notes = "Returns a greeting message along with an incrementing visitor count. This endpoint is publicly accessible.",
            response = IndexController.Message.class)
    public Message greeting() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        long visitorNumber = counter.incrementAndGet();
        String messageContent = "Congratulations you are visitor number: ";

        log.info("Requester [{}]: Greeting endpoint accessed. Visitor number: {}", requesterId, visitorNumber);

        return new Message(messageContent, visitorNumber);
    }

    /**
     * Record representing a simple message with an ID.
     *
     * @param content The message content.
     * @param id      A unique identifier for the message (visitor count in this case).
     */
    public record Message(String content, long id) {
    }
}