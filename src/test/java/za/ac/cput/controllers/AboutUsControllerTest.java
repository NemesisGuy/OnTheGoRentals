/*
package za.ac.cput.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import za.ac.cput.domain.entity.AboutUs;
import za.ac.cput.security.CustomerUserDetailsService;
import za.ac.cput.security.JwtUtilities;
import za.ac.cput.security.SpringSecurityConfig;
import za.ac.cput.service.IAboutUsService;

import java.time.LocalDateTime; // Correct import
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AboutUsController.class)
@Import(SpringSecurityConfig.class)
@ActiveProfiles("test")
class AboutUsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAboutUsService aboutUsService;

    @MockBean
    private JwtUtilities jwtUtilities;

    @MockBean
    private CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper; // Though not used for GETs, good to have.

    private AboutUs aboutUs1, aboutUs2;
    private UUID aboutUs1Uuid, aboutUs2Uuid;

    @BeforeEach
    void setUp() {
        aboutUs1Uuid = UUID.randomUUID();
        aboutUs1 = new AboutUs.Builder()
                .setId(1)
                .setUuid(aboutUs1Uuid)
                .setAddress("123 Main St, Anytown")
                .setOfficeHours("Mon-Fri 9am-5pm")
                .setEmail("contact@example.com")
                .setTelephone("555-1234")
                .setWhatsApp("555-5678")
                .setCreatedAt(LocalDateTime.now().minusDays(10))
                .setUpdatedAt(LocalDateTime.now().minusDays(5))
                .setDeleted(false)
                .build();

        aboutUs2Uuid = UUID.randomUUID();
        aboutUs2 = new AboutUs.Builder()
                .setId(2)
                .setUuid(aboutUs2Uuid)
                .setAddress("456 Oak St, Otherville")
                .setOfficeHours("Mon-Sat 10am-6pm")
                .setEmail("info@otherexample.com")
                .setTelephone("555-8765")
                .setWhatsApp("555-4321")
                .setCreatedAt(LocalDateTime.now().minusDays(2))
                .setUpdatedAt(LocalDateTime.now().minusDays(1))
                .setDeleted(false)
                .build();

        when(jwtUtilities.validateToken(anyString())).thenReturn(true);
        when(jwtUtilities.extractUserEmail(anyString())).thenReturn("testuser@example.com");
    }

    @Test
    void read_whenAboutUsExists_shouldReturnAboutUsDto() throws Exception {
        when(aboutUsService.read(1)).thenReturn(aboutUs1);

        mockMvc.perform(get("/api/v1/about-us/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                // Assuming your DTO and mapper expose these fields and they are wrapped in "data"
                .andExpect(jsonPath("$.data.uuid", is(aboutUs1Uuid.toString())))
                .andExpect(jsonPath("$.data.address", is(aboutUs1.getAddress())))
                .andExpect(jsonPath("$.data.email", is(aboutUs1.getEmail())));
    }

    @Test
    void read_whenAboutUsNotFound_shouldReturnNotFound() throws Exception {
        when(aboutUsService.read(99)).thenReturn(null);

        mockMvc.perform(get("/api/v1/about-us/{id}", 99)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
        // If your GlobalExceptionHandler wraps this specific 404 into ApiResponse:
        // .andExpect(jsonPath("$.status", is("fail"))) // Or whatever your handler sets
        // .andExpect(jsonPath("$.errors[0].message", containsString("About Us content not found")));
    }

    @Test
    void getAll_whenAboutUsEntriesExist_shouldReturnListOfDtos() throws Exception {
        List<AboutUs> aboutUsList = Arrays.asList(aboutUs1, aboutUs2);
        when(aboutUsService.getAll()).thenReturn(aboutUsList);

        mockMvc.perform(get("/api/v1/about-us")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].uuid", is(aboutUs1Uuid.toString())))
                .andExpect(jsonPath("$.data[1].uuid", is(aboutUs2Uuid.toString())));
    }

    @Test
    void getAll_whenNoAboutUsEntries_shouldReturnNoContent() throws Exception {
        when(aboutUsService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/about-us")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
        // If global wrapper changes 204 body:
        // .andExpect(jsonPath("$.data").isEmpty())
        // .andExpect(jsonPath("$.status", is("success")));
    }

    @Test
    void getLatest_whenAboutUsEntriesExist_shouldReturnLatestDto() throws Exception {
        List<AboutUs> aboutUsList = Arrays.asList(aboutUs1, aboutUs2); // aboutUs2 is later if ordered by ID or creation
        when(aboutUsService.getAll()).thenReturn(aboutUsList);

        mockMvc.perform(get("/api/v1/about-us/latest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.uuid", is(aboutUs2Uuid.toString())))
                .andExpect(jsonPath("$.data.address", is(aboutUs2.getAddress())));
    }

    @Test
    void getLatest_whenNoAboutUsEntries_shouldReturnNotFound() throws Exception {
        when(aboutUsService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/about-us/latest")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
        // If global wrapper changes 404 body:
        // .andExpect(jsonPath("$.status", is("fail")))
    }
}*/
