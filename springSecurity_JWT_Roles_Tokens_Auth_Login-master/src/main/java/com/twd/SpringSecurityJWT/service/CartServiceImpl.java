package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.Bien;
import com.twd.SpringSecurityJWT.entity.Cart;
import com.twd.SpringSecurityJWT.repository.BienRepo;
import com.twd.SpringSecurityJWT.repository.CartRepository;
import com.twd.SpringSecurityJWT.repository.CommandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final BienRepo bienRepository;




    @Override
    public Cart addCart(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    public Cart updateCart(Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    public void removeCartById(int idCart) {
        cartRepository.deleteById(idCart);
    }



    @Override
    public List<Cart> getAllCart() {
        return null;
    }

    public List<Bien> getBiensInCart(int cartId) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            return null;
        }
        Set<Bien> biens = cart.getBiens();
        List<Integer> bienIds = biens.stream()
                .map(Bien::getId)
                .collect(Collectors.toList());
        return bienRepository.findAllById(bienIds);
    }


    public boolean deleteBienFromCart(int cartId, int bienId) {
        Cart cart = cartRepository.findById(cartId).orElse(null);

        if (cart == null) {
            return false;
        }

        Bien bienToRemove = cart.getBiens().stream()
                .filter(bien -> bien.getId() == bienId)
                .findFirst()
                .orElse(null);

        // If the Bien doesn't exist in the Cart, return false
        if (bienToRemove == null) {
            return false;
        }

        // Remove the Bien from the Cart's set of Biens
        cart.getBiens().remove(bienToRemove);

        // Save the updated Cart back to the database
        cartRepository.save(cart);

        // Return true to indicate that the Bien was successfully removed from the Cart
        return true;
    }


    @Override
    public Cart getCartById(int cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));
    }



}



