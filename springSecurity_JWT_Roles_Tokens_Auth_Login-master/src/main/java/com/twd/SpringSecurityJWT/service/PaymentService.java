package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.checkoutPayment;

public interface PaymentService {
    checkoutPayment savePayment(checkoutPayment payment);
}

