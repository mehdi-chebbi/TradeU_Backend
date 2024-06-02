package com.twd.SpringSecurityJWT.service;


import com.twd.SpringSecurityJWT.entity.Commande;
import com.twd.SpringSecurityJWT.entity.Users;

import java.util.List;
import java.time.LocalDate;
import java.util.Map;


public interface CommandeService {
    void deleteCommande(int commandeId);


    Commande createCommandeFromCartWithCoupon(Integer cartId, String couponCode, Users currentUser);
    Commande createCommandeFromCart(Integer cartId, Users currentUser);
    Map<LocalDate, Float> getTotalRevenueByDay();

    List<Commande> getAllCommandes();

}
