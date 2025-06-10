/*
package za.ac.cput.controllers.security;

import com.fasterxml.jackson.databind.ObjectMapper;
// No Cookie import needed
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
// import za.ac.cput.domain.dto.response.RentalResponseDTO; // Assuming it has uuid
// import za.ac.cput.domain.dto.response.UserResponseDTO; // Assuming it has uuid, email, firstName, lastName
import za.ac.cput.domain.entity.Rental;
import za.ac.cput.domain.entity.security.Role;
import za.ac.cput.domain.entity.security.RoleName;
import za.ac.cput.domain.entity.security.User;
import za.ac.cput.domain.enums.RentalStatus;
import za.ac.cput.security.CustomerUserDetailsService;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.security.SpringSecurityConfig;
import za.ac.cput.service.IRentalService;
import za.ac.cput.service.IUserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@Import(SpringSecurityConfig.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userService;

    @MockBean
    private IRentalService rentalService;

    @MockBean
    private JwtUtilities jwtUtilities;

    @MockBean
    private CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private User sampleUser;
    private Role sampleRoleUser;
    // UserResponseDTO and RentalResponseDTO are mapped by controller,
    // so we don't need instances of them in the test setup, just of the entities.

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        sampleRoleUser = Role.builder().id(1).roleName(RoleName.USER).build();
        sampleUser = User.builder()
                .id(1) // Assuming User entity ID is Integer
                .uuid(UUID.randomUUID())
                .firstName("Test")
                .lastName("User")
                .email("testuser@example.com")
                .password("encodedPassword")
                .roles(Collections.singletonList(sampleRoleUser))
                .authProvider(za.ac.cput.domain.enums.AuthProvider.LOCAL)
                .deleted(false).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(jwtUtilities.validateToken(anyString())).thenReturn(true);
        when(jwtUtilities.extractUserEmail(anyString())).thenReturn(sampleUser.getEmail());

        when(customerUserDetailsService.loadUserByUsername(sampleUser.getEmail()))
                .thenReturn(new org.springframework.security.core.userdetails.User(
                        sampleUser.getEmail(), sampleUser.getPassword(), sampleUser.getAuthorities()
                ));
    }

    private void mockAuthenticatedUser(User user) {
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleNameEnum().name()))
                .collect(Collectors.toList());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                null,
                authorities
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void getCurrentUserProfile_whenAuthenticated_shouldReturnUserProfile() throws Exception {
        mockAuthenticatedUser(sampleUser);
        when(userService.read(sampleUser.getEmail())).thenReturn(sampleUser);

        mockMvc.perform(get("/api/v1/users/me/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email", is(sampleUser.getEmail())))
                .andExpect(jsonPath("$.data.firstName", is(sampleUser.getFirstName())));

        verify(userService).read(sampleUser.getEmail());
    }

    @Test
    void getCurrentUserProfile_whenUserNotFound_shouldReturnNotFound() throws Exception {
        mockAuthenticatedUser(sampleUser);
        when(userService.read(sampleUser.getEmail())).thenReturn(null);

        mockMvc.perform(get("/api/v1/users/me/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is("fail")))
                .andExpect(jsonPath("$.errors[0].message", is("User profile not found for authenticated user: " + sampleUser.getEmail())));
    }


    @Test
    void updateCurrentUserProfile_whenAuthenticatedAndValidData_shouldUpdateAndReturnProfile() throws Exception {
        mockAuthenticatedUser(sampleUser);
        UserUpdateRequestDTO updateDTO = new UserUpdateRequestDTO("UpdatedFirst", "UpdatedLast");

        User userAfterRead = User.builder() // Simulate what userService.read returns
                .id(sampleUser.getId()).uuid(sampleUser.getUuid()).email(sampleUser.getEmail())
                .firstName(sampleUser.getFirstName()).lastName(sampleUser.getLastName())
                .roles(sampleUser.getRoles()) // ensure all relevant fields are there
                .build();

        User updatedUserEntity = userAfterRead.toBuilder()
                .firstName(updateDTO.getFirstName())
                .lastName(updateDTO.getLastName())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userService.read(sampleUser.getEmail())).thenReturn(userAfterRead);
        when(userService.update(eq(sampleUser.getId()), any(User.class))).thenReturn(updatedUserEntity);

        mockMvc.perform(put("/api/v1/users/me/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email", is(sampleUser.getEmail())))
                .andExpect(jsonPath("$.data.firstName", is("UpdatedFirst")))
                .andExpect(jsonPath("$.data.lastName", is("UpdatedLast")));

        verify(userService).read(sampleUser.getEmail());
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).update(eq(sampleUser.getId()), userCaptor.capture());
        assertEquals("UpdatedFirst", userCaptor.getValue().getFirstName());
        assertEquals("UpdatedLast", userCaptor.getValue().getLastName());
    }

    @Test
    void updateCurrentUserProfile_whenNoChanges_shouldReturnCurrentProfile() throws Exception {
        mockAuthenticatedUser(sampleUser);
        UserUpdateRequestDTO updateDTO = new UserUpdateRequestDTO(sampleUser.getFirstName(), sampleUser.getLastName());

        when(userService.read(sampleUser.getEmail())).thenReturn(sampleUser);

        mockMvc.perform(put("/api/v1/users/me/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.firstName", is(sampleUser.getFirstName())));

        verify(userService).read(sampleUser.getEmail());
        verify(userService, never()).update(anyInt(), any(User.class));
    }

    @Test
    void getCurrentUserRentalHistory_whenAuthenticatedAndHasRentals_shouldReturnRentalList() throws Exception {
        mockAuthenticatedUser(sampleUser);

        UUID rental1Uuid = UUID.randomUUID();
        UUID rental2Uuid = UUID.randomUUID();
        Rental rental1 = Rental.builder().setId(1).setUuid(rental1Uuid).setUser(sampleUser).setStatus(RentalStatus.ACTIVE).build();
        Rental rental2 = Rental.builder().setId(2).setUuid(rental2Uuid).setUser(sampleUser).setStatus(RentalStatus.COMPLETED).build();
        List<Rental> rentalHistory = Arrays.asList(rental1, rental2);

        when(userService.read(sampleUser.getEmail())).thenReturn(sampleUser);
        when(rentalService.getRentalHistoryByUser(sampleUser)).thenReturn(rentalHistory);

        // Assuming RentalMapper.toDtoList creates DTOs that have a 'uuid' field (or 'id' if that's preferred)
        mockMvc.perform(get("/api/v1/users/me/rental-history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].uuid", is(rental1Uuid.toString()))) // Check UUID
                .andExpect(jsonPath("$.data[1].uuid", is(rental2Uuid.toString())));

        verify(userService).read(sampleUser.getEmail());
        verify(rentalService).getRentalHistoryByUser(sampleUser);
    }

    @Test
    void getCurrentUserRentalHistory_whenNoRentals_shouldReturnNoContent() throws Exception {
        mockAuthenticatedUser(sampleUser);
        when(userService.read(sampleUser.getEmail())).thenReturn(sampleUser);
        when(rentalService.getRentalHistoryByUser(sampleUser)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/users/me/rental-history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void getCurrentUserProfile_whenUnauthenticated_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users/me/profile")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("Unauthorized - Please log in again"));
    }
}*/
