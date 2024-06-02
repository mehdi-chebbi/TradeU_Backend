package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.Reponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ReponseRepository extends JpaRepository<Reponse,Integer> {
}
