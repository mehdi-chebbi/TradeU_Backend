package com.twd.SpringSecurityJWT.repository;


import com.twd.SpringSecurityJWT.entity.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CommandeRepository extends JpaRepository<Commande, Integer> {


}
