package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.ReponseSondage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReponseRepo extends JpaRepository<ReponseSondage, Integer> {
    List<ReponseSondage> findReponseSondageByQuestion_Id(Integer idQuestion);


}
