package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.Categorie;
import com.twd.SpringSecurityJWT.repository.CategorieRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
@Repository
public class ICategorieServiceImp implements ICategorieService {

    CategorieRepo CategorieRepo;

    @Override
    public Categorie addcategorie(Categorie categorie) {
        return CategorieRepo.save(categorie);
    }

    @Override
    public Categorie updatecategorie(Categorie categorie) {
        return CategorieRepo.save(categorie);
    }

    @Override
    public void removecategorie(Integer idcategorie) {
        CategorieRepo.deleteById(idcategorie);
    }
    @Override
    public Optional<Categorie> retrivecategorie(Integer idcategorie) {
        return CategorieRepo.findById(idcategorie);
    }
    @Override
    public List<Categorie> retriveAllcategorie() {
        return CategorieRepo.findAll();
    }



}
