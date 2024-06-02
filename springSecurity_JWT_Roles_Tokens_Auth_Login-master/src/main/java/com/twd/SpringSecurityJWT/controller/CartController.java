package com.twd.SpringSecurityJWT.controller;


import com.twd.SpringSecurityJWT.entity.Bien;
import com.twd.SpringSecurityJWT.entity.Cart;
import com.twd.SpringSecurityJWT.entity.Users;
import com.twd.SpringSecurityJWT.repository.CartRepository;
import com.twd.SpringSecurityJWT.repository.BienRepo;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.service.CartService;
import com.twd.SpringSecurityJWT.service.JWTUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.twd.SpringSecurityJWT.service.IBienService;


import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8083")
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("CartController")
public class CartController {

    private final CartService cartService;
    private final CartRepository cartRepository;
    private final BienRepo bienRepository;
    private JWTUtils jwtUtils;
    private OurUserRepo userRepository;

    private IBienService bienService;


    @GetMapping("/Get-All-Cart")
    public List<Cart> getAllCart() {
        return cartService.getAllCart();
    }

    @PostMapping("/add-cart")
    public Cart addCart(@RequestBody Cart cart) {
        return cartService.addCart(cart);
    }

    @PutMapping("/update-Cart")
    public Cart updateCart(@RequestBody Cart cart) {
        return cartService.updateCart(cart);
    }

    @DeleteMapping("/Delete-Cart/{idCart}")
    public void removeCart(@PathVariable("idCart") int idCart) {
        cartService.removeCartById(idCart);
    }

    @PostMapping("/addCard")
    public ResponseEntity<String> addBiensToCart(@RequestBody List<Integer> bienIds, @RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Invalid or missing token
            }

            String jwtToken = token.substring(7);
            String userEmail = jwtUtils.extractUsername(jwtToken);

            Optional<Users> userOptional = userRepository.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // User not found or unauthorized
            }

            Users currentUser = userOptional.get();

            Cart cart = currentUser.getCart();
            if (cart == null) {
                cart = new Cart();
                cart.setUser(currentUser);
                currentUser.setCart(cart);
                cartRepository.save(cart);
                userRepository.save(currentUser);


            }

            for (int bienId : bienIds) {
                Bien bien = bienRepository.findById(bienId)
                        .orElseThrow(() -> new RuntimeException("Bien not found"));

                // Check if the bien is already associated with the cart
                if (!cart.getBiens().contains(bien)) {
                    bien.getCarts().add(cart);
                    cart.getBiens().add(bien);
                }
            }

            // Save biens before saving the cart
            for (int bienId : bienIds) {
                Bien bien = bienRepository.findById(bienId)
                        .orElseThrow(() -> new RuntimeException("Bien not found"));
                bienRepository.save(bien);
            }


            return ResponseEntity.ok("Biens ajoutés au panier avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'ajout des biens au panier : " + e.getMessage());
        }
    }







    @GetMapping("/cart/{cartId}/biens")
    public ResponseEntity<List<Bien>> getBiensInCart(@PathVariable int cartId) {
        List<Bien> biens = cartService.getBiensInCart(cartId);
        if (biens != null) {
            return ResponseEntity.ok(biens);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/carts/{cartId}")
    public ResponseEntity<Cart> getCartById(@PathVariable int cartId) {
        Cart cart = cartService.getCartById(cartId);
        if (cart != null) {
            return ResponseEntity.ok(cart);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{cartId}/biens/{bienId}")
    public ResponseEntity<Void> deleteBienFromCart(@PathVariable int cartId, @PathVariable int bienId, Authentication authentication) {
        Users currentUser = (Users) authentication.getPrincipal();

        boolean deleted = cartService.deleteBienFromCart(cartId, bienId);
        if (deleted) {
            return ResponseEntity.noContent().build(); // Successful deletion
        }
        return ResponseEntity.notFound().build(); // Either Cart or Bien not found
    }


    @GetMapping("/get-current-user")
    public Users getCurrentUser(Authentication authentication){
        Users currentUser = (Users) authentication.getPrincipal();
        return currentUser;

    }



}


