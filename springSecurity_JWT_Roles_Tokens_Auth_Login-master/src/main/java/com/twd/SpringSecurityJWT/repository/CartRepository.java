package com.twd.SpringSecurityJWT.repository;


import com.twd.SpringSecurityJWT.entity.Cart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends CrudRepository<Cart, Integer> {
}
