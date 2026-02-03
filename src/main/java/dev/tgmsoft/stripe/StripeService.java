package dev.tgmsoft.stripe;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
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

    public PaymentIntent charge10Dollars() throws Exception {

        Configure();

        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(1000L) // 10.00 USD (amount is in cents)
                        .setCurrency("usd")
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods
                                        .builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        return PaymentIntent.create(params);
    }
}
