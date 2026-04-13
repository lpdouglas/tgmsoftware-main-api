package dev.tgmsoft.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StripeController {
    
    @GetMapping("/create-payment")
    public String createPayment() {
        return "Payment created";
    }
    
}
