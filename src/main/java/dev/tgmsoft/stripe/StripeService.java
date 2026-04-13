package dev.tgmsoft.stripe;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class StripeService {

    @Value("${stripe.key}")
    private String stripeKey;

    public void Configure() {
        if (Stripe.apiKey != null) return;
        Stripe.apiKey = stripeKey;
        //log.info("STRIPE API KEY: {}", stripeKey);
    }

    public Map<String, String> charge(Integer amount) throws Exception {
    Configure();

    PaymentIntentCreateParams params =
            PaymentIntentCreateParams.builder()
                    .setAmount(amount * 100L)
                    .setCurrency("usd")
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods
                                    .builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

    PaymentIntent paymentIntent = PaymentIntent.create(params);
    
    // Return the client secret to the frontend
    Map<String, String> response = new HashMap<>();
    response.put("clientSecret", paymentIntent.getClientSecret());
    return response;
}

}
