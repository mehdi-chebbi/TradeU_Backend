package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.Categorie;

import java.util.List;
import java.util.Optional;

public interface ICategorieService {
    Categorie addcategorie (Categorie categorie);
    Categorie updatecategorie (Categorie categorie);
    void removecategorie (Integer idcategorie);
    Optional<Categorie> retrivecategorie(Integer id);
    List<Categorie> retriveAllcategorie();

}
