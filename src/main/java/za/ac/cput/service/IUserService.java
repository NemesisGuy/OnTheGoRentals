package za.ac.cput.service;
/**
 * Author: Peter Buckingham (220165289)
 */


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import za.ac.cput.domain.dto.request.LoginDto;
import za.ac.cput.domain.dto.request.RegisterDto;
import za.ac.cput.domain.dto.response.AuthResponseDto;
import za.ac.cput.domain.security.Role;
import za.ac.cput.domain.security.User;

import java.util.List;


public interface IUserService {


    String authenticate(LoginDto loginDto);

    ResponseEntity<?> register(RegisterDto registerDto);

    Role saveRole(Role role);

    User saverUser(User user);

    List<User> getAll();

    User read(Integer id);

    User update(Integer id, User user);

    User read(String userEmail);

    ResponseEntity<AuthResponseDto> registerAndReturnAuthResponse(@Valid RegisterDto registerDto);

    /*   User readByEmail(String email);*/
}
