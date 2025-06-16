package za.ac.cput.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import za.ac.cput.utils.SecurityUtils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * IndexController.java
 * A simple controller providing a basic greeting message with a visitor counter.
 * This can be used for health checks or as a simple landing page for the API.
 *
 * @author Peter Buckingham (220165289)
 * @version 2.0
 */
@RestController
@Tag(name = "Index / Greeting", description = "Provides a simple greeting message and API health check.")
public class IndexController {

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);
    private final AtomicLong counter = new AtomicLong();

    /**
     * Handles requests to various root/home paths and returns a greeting message
     * including an incrementing visitor count. This endpoint is publicly accessible.
     *
     * @return A {@link Message} object containing the greeting and visitor count.
     */
    @Operation(summary = "Get Greeting Message", description = "Returns a greeting message with a visitor count. Useful as a simple API health check.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Greeting message returned successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class)))
    })
    @GetMapping({"/", "/home", "/index", "/api/home", "/api/hello", "/api/index", "/api/greeting"})
    public Message greeting() {
        String requesterId = SecurityUtils.getRequesterIdentifier();
        long visitorNumber = counter.incrementAndGet();
        String messageContent = "Congratulations, you are visitor number: ";

        log.info("Requester [{}]: Greeting endpoint accessed. Visitor number: {}", requesterId, visitorNumber);

        return new Message(messageContent, visitorNumber);
    }

    /**
     * A record representing a simple message with an ID.
     * This is used as the JSON response body for the greeting endpoint.
     *
     * @param content The message content.
     * @param id      A unique identifier for the message (the visitor count in this case).
     */
    public record Message(String content, long id) {
    }
}