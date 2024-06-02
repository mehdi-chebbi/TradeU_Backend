package com.twd.SpringSecurityJWT.service;


import com.twd.SpringSecurityJWT.entity.Coupon;

import java.util.List;
import java.util.Map;

public interface CouponService {

    Coupon generateCoupon();
    Map<String, Integer> getCouponStatistics();

    Coupon createCoupon(Coupon coupon);

    Coupon getCouponByCode(String code);

    boolean redeemCoupon(String code);

    List<Coupon> getAllCoupons();
}

