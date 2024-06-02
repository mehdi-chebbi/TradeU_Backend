package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.checkoutPayment;
import com.twd.SpringSecurityJWT.repository.CheckoutPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckoutPaymentServiceImpl implements PaymentService {

    private final CheckoutPaymentRepository checkoutPaymentRepository;

    @Autowired
    public CheckoutPaymentServiceImpl(CheckoutPaymentRepository checkoutPaymentRepository) {
        this.checkoutPaymentRepository = checkoutPaymentRepository;
    }

    @Override
    public checkoutPayment savePayment(checkoutPayment payment) {
        return checkoutPaymentRepository.save(payment);
    }
}
