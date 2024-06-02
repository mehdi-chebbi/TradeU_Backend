package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.*;
import com.twd.SpringSecurityJWT.repository.CartRepository;
import com.twd.SpringSecurityJWT.repository.CommandeRepository;
import com.twd.SpringSecurityJWT.repository.CouponRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final CouponRepository couponRepository;
    private final CouponService couponService;
    @Override
    public void deleteCommande(int commandeId) {
        commandeRepository.deleteById(commandeId);
    }


    public Commande createCommandeFromCart(Integer cartId, Users currentUser) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found for ID: " + cartId));

        float totalPrice = calculateTotalPriceForCart(cart);

        Commande commande = new Commande();
        commande.setCart(cart);
        commande.setPrixTotal(totalPrice);
        commande.setDateEnregistrement(LocalDate.now());

        return commandeRepository.save(commande);
    }



    public Commande createCommandeFromCartWithCoupon(Integer cartId, String couponCode, Users currentUser) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found for ID: " + cartId));

        float totalPrice = calculateTotalPriceForCart(cart);

        // Check if a coupon code is provided and apply discount if valid
        if (couponCode != null && !couponCode.isEmpty()) {
            Coupon coupon = couponService.getCouponByCode(couponCode);
            if (coupon != null && !coupon.isRedeemed()) {
                float discountAmount = calculateDiscount(totalPrice);
                totalPrice -= discountAmount;
                coupon.setRedeemed(true);
                couponRepository.save(coupon);

            }
        }

        Commande commande = new Commande();
        commande.setCart(cart);
        commande.setPrixTotal(totalPrice);
        commande.setDateEnregistrement(LocalDate.now());

        return commandeRepository.save(commande);
    }

    private float calculateTotalPriceForCart(Cart cart) {
        float totalPrice = 0.0f;
        for (Bien bien : cart.getBiens()) {
            totalPrice += bien.getPrix();
        }
        return totalPrice;
    }
    @Override
    public List<Commande> getAllCommandes() {
        return commandeRepository.findAll();
    }

    @Override
    public Map<LocalDate, Float> getTotalRevenueByDay() {
        List<Commande> commandes = commandeRepository.findAll();
        Map<LocalDate, Float> totalRevenueByDay = new HashMap<>();

        // Aggregate total revenue for each day
        for (Commande commande : commandes) {
            LocalDate day = commande.getDateEnregistrement();
            float totalPrice = commande.getPrixTotal();
            totalRevenueByDay.put(day, totalRevenueByDay.getOrDefault(day, 0f) + totalPrice);
        }

        return totalRevenueByDay;
    }




    private float calculateDiscount(float totalPrice) {
        return totalPrice * 0.20f; // Apply 20% discount
    }


}
