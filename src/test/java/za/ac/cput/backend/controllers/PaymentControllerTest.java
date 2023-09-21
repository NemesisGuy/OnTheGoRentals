/**package za.ac.cput.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import za.ac.cput.controllers.PaymentController;
import za.ac.cput.domain.Payment;
import za.ac.cput.service.PaymentService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    private List<Payment> paymentList;

    @BeforeEach
    void setUp() {
        // Initialize test data
        paymentList = new ArrayList<>();
        paymentList.add(new Payment());
    }

    @Test
    void testGetAllPayments() throws Exception {
        when(paymentService.getAllPayments()).thenReturn(paymentList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/payments/list/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(paymentList.size()));
    }

    @Test
    void testGetPaymentsByUser() throws Exception {
        int userId = 1;
        when(paymentService.getPaymentsByUserId(userId)).thenReturn(paymentList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/payments/list/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(paymentList.size()));
    }

    @Test
    void testCreatePayment() throws Exception {
        Payment newPayment = new Payment(/* Create a new Payment object with required parameters );
        when(paymentService.createPayment(Mockito.any(Payment.class))).thenReturn(newPayment);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/payments/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPayment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newPayment.getId()));
    }

    @Test
    void testUpdatePayment() throws Exception {
        Payment updatedPayment = new Payment(/* Create an updated Payment object with required parameters );
        when(paymentService.updatePayment(Mockito.any(Payment.class))).thenReturn(updatedPayment);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/payments/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPayment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedPayment.getId()));
    }

    @Test
    void testDeletePayment() throws Exception {
        int paymentId = 1;
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/payments/delete/{paymentId}", paymentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
*/