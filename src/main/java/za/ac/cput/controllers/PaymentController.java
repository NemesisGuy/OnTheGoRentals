package za.ac.cput.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Payment;
import za.ac.cput.service.IPaymentService;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private IPaymentService service;

    @PostMapping("/create")
    public Payment processPayment(@RequestBody Payment payment) {
        return service.create(payment);
    }

    @GetMapping("/read/{id}")
    public Payment getPayment(@PathVariable int id) {
        return service.read(id);
    }

    @GetMapping("/get-all")
    public List<Payment> getAllPayments() {
        return service.getAllPayments();
    }

    @PutMapping("/update")
    public Payment updatePayment(@RequestBody Payment payment) {
        return service.update(payment);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deletePayment(@PathVariable int id) {
        return service.delete(id);
    }
}
