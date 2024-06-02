package com.twd.SpringSecurityJWT.repository;
import com.twd.SpringSecurityJWT.entity.Bien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface BienRepo extends JpaRepository<Bien, Integer> {
    @Query("SELECT b FROM Bien b WHERE b.categorie.id = :categorieId")
    List<Bien> findByCategorie(Integer categorieId);
    List<Bien> findBienByBadfeedcountIsGreaterThan(Integer num);
}
