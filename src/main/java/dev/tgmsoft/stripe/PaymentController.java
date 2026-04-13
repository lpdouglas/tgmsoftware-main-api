package dev.tgmsoft.stripe;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/donate")
@AllArgsConstructor
public class PaymentController {

    public StripeService stripeService;

    @PostMapping("/{amount}")
    public Map<String, String> createPayment(@PathVariable("amount") Integer amount) throws Exception {
        System.out.println("PaymentIntent created with amount: " + amount);

        Map<String, String> intent = stripeService.charge(amount);

        return intent;
    }
}
