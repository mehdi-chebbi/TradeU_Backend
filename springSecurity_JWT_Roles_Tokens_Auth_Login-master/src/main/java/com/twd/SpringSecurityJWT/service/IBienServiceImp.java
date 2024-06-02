package com.twd.SpringSecurityJWT.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.twd.SpringSecurityJWT.repository.CategorieRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import com.twd.SpringSecurityJWT.entity.Bien ;
import com.twd.SpringSecurityJWT.repository.BienRepo ;


@Service
@AllArgsConstructor
@Slf4j
@Repository
public class IBienServiceImp implements IBienService{

    BienRepo BienRepo;

    private CategorieRepo CategorieRepo;

    @Override
    public Bien addbien(Bien bien) {
        return BienRepo.save(bien);
    }

    @Override
    public Bien updatebien(Bien bien) {
        return BienRepo.save(bien);
    }

    @Override
    public void removebien(Integer idbien) {
        BienRepo.deleteById(idbien);
    }
    @Override
    public Optional<Bien> retrivebien(Integer idBien) {
        return BienRepo.findById(idBien);
    }
    @Override
    public List<Bien> retriveAllBien() {
        return BienRepo.findAll();
    }
    @Override
    public void changerEtatAutorisationBien(Integer idBien, boolean autorise) {
        Optional<Bien> bienOptional = BienRepo.findById(idBien);
        if (bienOptional.isPresent()) {
            Bien bien = bienOptional.get();
            bien.setAutorise(autorise);
            BienRepo.save(bien);
        } else {
            // Gérer le cas où le bien n'est pas trouvé
            throw new RuntimeException("Le bien avec l'ID " + idBien + " n'existe pas.");
        }
    }

    @Override
    public List<Bien> retrieveAllAuthorizedBien() {
        List<Bien> allBiens = BienRepo.findAll(); // Supposons que bienRepository est votre repository pour l'entité Bien
        List<Bien> authorizedBiens = new ArrayList<>();

        for (Bien bien : allBiens) {
            if (bien.isAutorise()) {
                authorizedBiens.add(bien);
            }
        }

        return authorizedBiens;
    }
    @Transactional
    @Override
    public List<Bien> retrieveBiensByCategoryId(Integer categorieId) {
        // Implémentez votre logique de récupération des biens par ID de catégorie ici
        // Utilisez votre repository BienRepo pour interagir avec la base de données
        // Retournez la liste des biens associés à la catégorie spécifiée
        List<Bien> biens = BienRepo.findByCategorie(categorieId);

        return biens;
    }
@Override
    public void updateBiensCategorieToNull(Integer categorieId) {
        // Rechercher tous les biens associés à la catégorie à supprimer
        List<Bien> biensToUpdate = BienRepo.findByCategorie(categorieId);

        // Mettre à jour chaque bien pour définir categorie_id à null
        for (Bien bien : biensToUpdate) {
            bien.setCategorie(null);
            // Enregistrez les modifications dans la base de données
            BienRepo.save(bien);
        }
    }

    @Override
    public List<Bien> getAllBiens() {
        return BienRepo.findAll();
    }
    @Transactional
    @Override
    public void updateBadFeedCount(Integer bienId) {
        // Retrieve the Bien entity from the database
        Optional<Bien> optionalBien = BienRepo.findById(bienId);

        if (optionalBien.isPresent()) {
            Bien bien = optionalBien.get();

            // Update the badfeedcount attribute
            bien.setBadfeedcount(bien.getBadfeedcount() + 1);

            // Save the updated Bien entity back to the database
            BienRepo.save(bien);
        } else log.error("bien Not found");
    }
    @Override
    public List<Bien> getBiensWithHighBadFeedCount() {
        return BienRepo.findBienByBadfeedcountIsGreaterThan(5);
    }


}
