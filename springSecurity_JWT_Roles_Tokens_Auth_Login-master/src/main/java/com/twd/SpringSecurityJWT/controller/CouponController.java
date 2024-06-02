package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.entity.Coupon;
import com.twd.SpringSecurityJWT.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
@CrossOrigin(origins = "http://localhost:8083")

@RestController
@RequestMapping("/coupons")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/generate")
    public ResponseEntity<Coupon> generateCoupon() {
        Coupon generatedCoupon = couponService.generateCoupon();
        return ResponseEntity.status(HttpStatus.CREATED).body(generatedCoupon);
    }

    @PostMapping("/create")
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        Coupon createdCoupon = couponService.createCoupon(coupon);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCoupon);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Coupon> getCouponByCode(@PathVariable String code) {
        Coupon coupon = couponService.getCouponByCode(code);
        if (coupon != null) {
            return ResponseEntity.ok(coupon);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/redeem/{code}")
    public ResponseEntity<String> redeemCoupon(@PathVariable String code) {
        boolean redeemed = couponService.redeemCoupon(code);
        if (redeemed) {
            return ResponseEntity.ok("Coupon redeemed successfully");
        } else {
            return ResponseEntity.badRequest().body("Coupon not found or already used");
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Integer>> getCouponStatistics() {
        Map<String, Integer> statistics = couponService.getCouponStatistics();
        return ResponseEntity.ok(statistics);
    }
}
