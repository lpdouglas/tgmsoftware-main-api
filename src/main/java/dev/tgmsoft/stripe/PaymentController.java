package dev.tgmsoft.stripe;

import com.stripe.model.PaymentIntent;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/donate")
@AllArgsConstructor
public class PaymentController {

    public StripeService stripeService;

    @PostMapping("/10")
    public Map<String, String> createPayment() throws Exception {
        PaymentIntent intent = stripeService.charge10Dollars();

        return Map.of(
                "clientSecret", intent.getClientSecret()
        );
    }
}
