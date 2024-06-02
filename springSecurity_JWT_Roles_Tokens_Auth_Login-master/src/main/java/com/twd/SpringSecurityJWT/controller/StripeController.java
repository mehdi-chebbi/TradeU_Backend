package com.twd.SpringSecurityJWT.controller;
import com.twd.SpringSecurityJWT.entity.Users;
import com.twd.SpringSecurityJWT.entity.checkoutPayment;
import com.twd.SpringSecurityJWT.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.stripe.Stripe;

@CrossOrigin(origins = "http://localhost:8083")
@RestController
@RequestMapping(value = "/apis")
public class StripeController {
    // create a Gson object
    private static Gson gson = new Gson();

    @PostMapping("/payment")
    /**

     Payment with Stripe checkout page*
     @throws StripeException*/
    public String paymentWithCheckoutPage(@RequestBody checkoutPayment payment) throws StripeException {

        init();
        // We create a  stripe session parameters
        SessionCreateParams params = SessionCreateParams.builder()
                // We will use the credit card payment method
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT).setSuccessUrl(payment.getSuccessUrl())
                .setCancelUrl(
                        payment.getCancelUrl())
                .addLineItem(
                        SessionCreateParams.LineItem.builder().setQuantity(payment.getQuantity())
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(payment.getCurrency()).setUnitAmount(payment.getAmount())
                                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData
                                                        .builder().setName(payment.getName()).build())
                                                .build())
                                .build())
                .build();
        // create a stripe session
        Session session = Session.create(params);
        Map<String, String> responseData = new HashMap<>();
        // We get the sessionId and we putted inside the response data you can get more info from the session object
        responseData.put("id", session.getId());
        // We can return only the sessionId as a String
        return gson.toJson(responseData);

    }

    private static void init() {
        Stripe.apiKey = "sk_test_51P8Q8tBTPaCAM14tIsU88uOKsyGlStR5ThH3u4Wbhq4MKPN6ouEOBBZFIbKV7gImp01eB1aIBts7bUxtSMV2Uf0800qXfV8OtY";
    }

    private final PaymentService paymentService;

    @Autowired
    public StripeController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}


