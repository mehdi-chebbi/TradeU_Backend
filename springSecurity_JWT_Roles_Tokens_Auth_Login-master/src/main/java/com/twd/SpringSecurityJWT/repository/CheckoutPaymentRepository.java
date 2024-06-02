package com.twd.SpringSecurityJWT.repository;
import com.twd.SpringSecurityJWT.entity.checkoutPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckoutPaymentRepository extends JpaRepository<checkoutPayment, Integer> {
    // You can add custom query methods here if needed
}

