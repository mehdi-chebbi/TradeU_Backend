// CommandeController.java
package com.twd.SpringSecurityJWT.controller;


import com.twd.SpringSecurityJWT.entity.Commande;
import com.twd.SpringSecurityJWT.entity.Users;
import com.twd.SpringSecurityJWT.repository.CommandeRepository;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.service.CommandeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:8083")

@RestController
@AllArgsConstructor
@RequestMapping("commandes")
public class CommandeController {

    CommandeService commandeService;
    CommandeRepository commandeRepository;
    OurUserRepo userRepository;


    @PostMapping("/create-commande/{cartId}")
    public ResponseEntity<Commande> createCommandeFromCart(@PathVariable("cartId") Integer cartId,
                                                           @RequestParam(name = "couponCode", required = false) String couponCode,
                                                           Authentication authentication) {
        try {
            Users currentUser = (Users) authentication.getPrincipal();
            Commande commande;

            if (couponCode != null) {
                commande = commandeService.createCommandeFromCartWithCoupon(cartId, couponCode, currentUser);
            } else {
                commande = commandeService.createCommandeFromCart(cartId, currentUser);
            }

            return ResponseEntity.ok(commande);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @GetMapping
    public ResponseEntity<List<Commande>> getAllCommandes() {
        List<Commande> commandes = commandeService.getAllCommandes();
        return ResponseEntity.ok().body(commandes);
    }
    @GetMapping("/total-revenue-by-day")
    public ResponseEntity<Map<LocalDate, Float>> getTotalRevenueByDay(Authentication authentication) {
        Users currentUser = (Users) authentication.getPrincipal();

        Map<LocalDate, Float> totalRevenueByDay = commandeService.getTotalRevenueByDay();
        return ResponseEntity.ok().body(totalRevenueByDay);
    }



}






