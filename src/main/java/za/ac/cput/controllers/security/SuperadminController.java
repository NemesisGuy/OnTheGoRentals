package za.ac.cput.controllers.security;
/**
 * Author: Peter Buckingham (220165289)
 */

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/superadmin")
@RequiredArgsConstructor
@Tag(name = "Superadmin", description = "Endpoints accessible only to superadministrators.")
@SecurityRequirement(name = "bearerAuth")
public class SuperadminController {


    //RessourceEndPoint:http://localhost:8087/api/superadmin/hi
    @Operation(
            summary = "Superadmin greeting",
            description = "A simple endpoint that returns a greeting. Used to verify superadmin access."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Not authenticated", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized as superadmin", content = @Content)
    })
    @GetMapping("/hi")
    public String sayHi() {
        return "Hi";
    }


}
