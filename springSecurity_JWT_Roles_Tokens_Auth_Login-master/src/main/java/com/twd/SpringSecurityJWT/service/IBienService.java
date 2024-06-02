package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.Bien;

import java.util.List;
import java.util.Optional;

public interface IBienService {
    Bien addbien (Bien bien);
    Bien updatebien (Bien bien);
    void removebien (Integer idbien);
    Optional<Bien> retrivebien(Integer id);
    List<Bien> retriveAllBien();
    void changerEtatAutorisationBien(Integer idBien, boolean autorise);
    List<Bien> retrieveAllAuthorizedBien();

    List<Bien>  retrieveBiensByCategoryId(Integer idCategorie);

    void updateBiensCategorieToNull(Integer categorieId);

    List<Bien> getAllBiens();
    public void updateBadFeedCount(Integer bienId);
    public List<Bien> getBiensWithHighBadFeedCount();



    // List<Bien> addListSkieurs(List<Bien> skieurs);

 //   List<skieur> findSkieursByPisteColor(Color color);
}
