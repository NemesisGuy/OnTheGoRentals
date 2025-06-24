package za.ac.cput.domain.dto.request;
/**
 * Author: Peter Buckingham (220165289)
 */

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterDto implements Serializable {

    //it's a Data Transfer Object for registration
    String firstName;
    String lastName;
    String email;
    String password;
}
