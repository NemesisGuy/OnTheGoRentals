package za.ac.cput.domain.dto.response;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private UUID uuid;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles; // List of role names (strings)
}