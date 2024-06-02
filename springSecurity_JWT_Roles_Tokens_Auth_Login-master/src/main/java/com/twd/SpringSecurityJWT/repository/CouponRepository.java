package com.twd.SpringSecurityJWT.repository;


import com.twd.SpringSecurityJWT.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Coupon findByCode(String code);

    @Query("SELECT COUNT(c) FROM Coupon c WHERE c.redeemed = true")
    int countRedeemed();

    @Query("SELECT COUNT(c) FROM Coupon c WHERE c.redeemed = false")
    int countActive();


}


