package com.twd.SpringSecurityJWT.service;


import com.twd.SpringSecurityJWT.entity.Coupon;
import com.twd.SpringSecurityJWT.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CouponServiceImpl implements CouponService {

    private static final int COUPON_CODE_LENGTH = 10;
    private static final String ALPHANUMERIC_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private CouponRepository couponRepository;
    private List<Coupon> coupons = new ArrayList<>();



    @Override
    public Coupon generateCoupon() {
        String couponCode = generateCouponCode();
        Coupon coupon = new Coupon();
        coupon.setCode(couponCode);
        coupon.setRedeemed(false);
        return couponRepository.save(coupon);
    }

    private String generateCouponCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < COUPON_CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(ALPHANUMERIC_CHARACTERS.length());
            sb.append(ALPHANUMERIC_CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }

    @Override
    public Map<String, Integer> getCouponStatistics() {
        Map<String, Integer> statistics = new HashMap<>();
        statistics.put("redeemed", couponRepository.countRedeemed());
        statistics.put("active", couponRepository.countActive());
        return statistics;
    }

    @Override
    public Coupon createCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }



    @Override
    public Coupon getCouponByCode(String code) {
        return couponRepository.findByCode(code);
    }


    @Override
    public boolean redeemCoupon(String code) {
        Coupon coupon = getCouponByCode(code);
        if (coupon != null && !coupon.isRedeemed()) {
            coupon.setRedeemed(true);
            couponRepository.save(coupon);

            return true;
        }
        return false;
    }

    @Override
    public List<Coupon> getAllCoupons() {
        return coupons;
    }

    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }


}
