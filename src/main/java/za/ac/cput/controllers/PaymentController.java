/**package za.ac.cput.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Payment;
import za.ac.cput.service.PaymentService;

import java.util.List;

/**
 * PaymentController.java
 * Author: Lonwabo Magazi-218331851
 * Date: September 2023


@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/list/all")
    public List<Payment> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return payments;
    }

    @GetMapping("/list/user/{userId}")
    public List<Payment> getPaymentsByUser(@PathVariable("userId") int userId) {
        List<Payment> payments = paymentService.getPaymentsByUserId(userId);
        return payments;
    }

    @GetMapping("/list/rental/{rentalId}")
    public List<Payment> getPaymentsByRental(@PathVariable("rentalId") int rentalId) {
        List<Payment> payments = paymentService.getPaymentsByRentalId(rentalId);
        return payments;
    }

    @PostMapping("/create")
    public Payment createPayment(@RequestBody Payment payment) {
        Payment newPayment = paymentService.createPayment(payment);
        return newPayment;
    }

    @PutMapping("/update")
    public Payment updatePayment(@RequestBody Payment payment) {
        Payment updatedPayment = paymentService.updatePayment(payment);
        return updatedPayment;
    }

    @DeleteMapping("/delete/{paymentId}")
    public void deletePayment(@PathVariable("paymentId") int paymentId) {
        paymentService.deletePayment(paymentId);
    }
}
*/