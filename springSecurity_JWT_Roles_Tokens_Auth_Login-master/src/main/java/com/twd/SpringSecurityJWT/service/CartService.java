package com.twd.SpringSecurityJWT.service;



import com.twd.SpringSecurityJWT.entity.Bien;
import com.twd.SpringSecurityJWT.entity.Cart;
import com.twd.SpringSecurityJWT.entity.Users;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface CartService {

    Cart addCart(Cart cart);
    Cart updateCart(Cart cart);
    void removeCartById(int idCart);

    List<Cart> getAllCart();
    List<Bien> getBiensInCart(int cartId);
    Cart getCartById(int cartId);
    boolean deleteBienFromCart(int cartId, int bienId);




}

