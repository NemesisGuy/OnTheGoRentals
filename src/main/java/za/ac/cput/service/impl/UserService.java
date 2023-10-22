package za.ac.cput.service.impl;
/**
 *
 * Author: Peter Buckingham (220165289)
 *
 */

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import za.ac.cput.domain.dto.BearerToken;
import za.ac.cput.domain.dto.LoginDto;
import za.ac.cput.domain.dto.RegisterDto;
import za.ac.cput.domain.security.Role;
import za.ac.cput.domain.security.RoleName;
import za.ac.cput.domain.security.User;
import za.ac.cput.repository.IRoleRepository;
import za.ac.cput.repository.IUserRepository;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.service.IUserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final AuthenticationManager authenticationManager ;
    private final IUserRepository iUserRepository ;
    private final IRoleRepository iRoleRepository ;
    private final PasswordEncoder passwordEncoder ;
    private final JwtUtilities jwtUtilities ;


    @Override
    public Role saveRole(Role role) {
        return iRoleRepository.save(role);
    }

    @Override
    public User saverUser(User user) {
        return iUserRepository.save(user);
    }

    @Override
    public ResponseEntity<?> register(RegisterDto registerDto) {
        if(iUserRepository.existsByEmail(registerDto.getEmail()))
        { return  new ResponseEntity<>("email is already taken !", HttpStatus.SEE_OTHER); }
        else
        { User user = new User();
            user.setEmail(registerDto.getEmail());
            user.setFirstName(registerDto.getFirstName());
            user.setLastName(registerDto.getLastName());
            user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
            //By Default , he/she is a simple user
            Role role = iRoleRepository.findByRoleName(RoleName.USER);
            user.setRoles(Collections.singletonList(role));
            iUserRepository.save(user);
            String token = jwtUtilities.generateToken(registerDto.getEmail(),Collections.singletonList(role.getRoleName()));
            return new ResponseEntity<>(new BearerToken(token , "Bearer "),HttpStatus.OK);

        }
        }

    @Override
    public String authenticate(LoginDto loginDto) {
      Authentication authentication= authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = iUserRepository.findByEmail(authentication.getName()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<String> rolesNames = new ArrayList<>();
        user.getRoles().forEach(r-> rolesNames.add(r.getRoleName()));
        String token = jwtUtilities.generateToken(user.getUsername(),rolesNames);
        return token;
    }
/////////////////////////////////

    public List<User> getAll() {
        return iUserRepository.findAll();
    }
    public User create(User user) {
        return iUserRepository.save(user);
    }
    public User read(Integer id) {
        return iUserRepository.findById(id).orElse(null);
    }

    @Override
    public User update(Integer id, User user) {
        if (iUserRepository.existsById(id)) {
            user.setId(id);
            return iUserRepository.save(user);
        }
        return null;
    }

    public User update(User user) {
        return iUserRepository.save(user);
    }
    public boolean delete(Integer id) {
        iUserRepository.deleteById(id);
        return !iUserRepository.existsById(id);
    }

/////////////////////////////////

    public User read(String email) {
        return iUserRepository.findByEmail(email).orElse(null);
    }

}

