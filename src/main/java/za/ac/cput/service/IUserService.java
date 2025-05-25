package za.ac.cput.service;
/**
 * Author: Peter Buckingham (220165289)
 */


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import za.ac.cput.domain.dto.request.LoginDto;
import za.ac.cput.domain.dto.request.RegisterDto;
import za.ac.cput.domain.dto.response.AuthResponseDto;
import za.ac.cput.domain.security.Role;
import za.ac.cput.domain.security.User;

import java.util.List;
import java.util.UUID;


public interface IUserService {


    String authenticate(LoginDto loginDto);

    ResponseEntity<?> register(RegisterDto registerDto);

    Role saveRole(Role role);

    User saverUser(User user);

    List<User> getAll();

    User read(Integer id);

    User read(UUID uuid);

    User read(String userEmail);
    User update(Integer id, User user);

    ResponseEntity<AuthResponseDto> registerAndReturnAuthResponse(@Valid RegisterDto registerDto);

    User create(User userToCreate);

    boolean delete(Integer id);

    /*   User readByEmail(String email);*/
}
