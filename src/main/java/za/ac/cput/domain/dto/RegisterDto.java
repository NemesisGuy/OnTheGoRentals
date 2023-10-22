package za.ac.cput.domain.dto;
/**
 *
 * Author: Peter Buckingham (220165289)
 *
 */
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterDto implements Serializable {

    //it's a Data Trasfer Object for registration
    String firstName ;
    String lastName ;
    String email;
    String password ;
}
