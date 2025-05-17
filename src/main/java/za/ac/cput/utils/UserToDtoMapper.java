package za.ac.cput.utils;

import za.ac.cput.domain.Rental;
import za.ac.cput.domain.dto.UserDTO;
import za.ac.cput.domain.security.User;

import java.util.ArrayList;
import java.util.List;

public class UserToDtoMapper {

    private UserDTO mapToUserDTO(User user) {
//        List<String> roleNames = new ArrayList<>();
//        user.getRoles().forEach(role -> roleNames.add(role.getRoleName()));
//        List<Rental> rentals = new ArrayList<>();

        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles()
               /* ,
                rentals*/
        );
    }
}
