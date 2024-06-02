package com.twd.SpringSecurityJWT.repository;


import com.twd.SpringSecurityJWT.entity.Bien;
import com.twd.SpringSecurityJWT.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategorieRepo extends JpaRepository<Categorie, Integer> {

}
