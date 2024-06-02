package com.twd.SpringSecurityJWT.controller;
import com.twd.SpringSecurityJWT.entity.Bien;
import com.twd.SpringSecurityJWT.entity.Categorie;
import com.twd.SpringSecurityJWT.service.AuthService;
import com.twd.SpringSecurityJWT.service.IBienService;
import com.twd.SpringSecurityJWT.service.ICategorieService;
import com.twd.SpringSecurityJWT.service.JWTUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.twd.SpringSecurityJWT.dto.ReqRes;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.entity.Users;

import java.util.*;


@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/public")
public class CategorieController {
    @Autowired
    private AuthService authService;

    @Autowired
    private OurUserRepo ourUserRepo;

    @Autowired
    private JWTUtils jwtUtils;

    ICategorieService ICategorieService;
    IBienService IBienService;



    @PostMapping("/add_categorie")
    public ResponseEntity<Object> addCategorie(@RequestHeader("Authorization") String token,
                                          @RequestBody Categorie categorie) {
        ReqRes response = new ReqRes(); // Assurez-vous que ReqRes est une classe correctement définie pour gérer les réponses

        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                String userEmail = jwtUtils.extractUsername(jwtToken);

                Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
                if (userOptional.isPresent()) {
                    Users user = userOptional.get();
                    categorie.setUser(user); // Associez l'utilisateur au Categorie

                    Categorie createdCategorie = ICategorieService.addcategorie(categorie);

                    response.setStatusCode(200);
                    response.setMessage("Categorie added successfully.");
                    return ResponseEntity.ok(response);
                } else {
                    response.setStatusCode(404);
                    response.setMessage("User not found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                response.setStatusCode(401);
                response.setMessage("Invalid or missing token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("An error occurred while adding the categorie: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping(value = "/update_categorie/{id}" )
    public ResponseEntity<Object> updateCategorie(@PathVariable Integer id,
                                                  @RequestHeader("Authorization") String token,
                                                  @RequestBody Categorie categorie) {
        ReqRes response = new ReqRes();

        try {
            if (token != null && token.startsWith("Bearer ")) {
                // Extraire le jeton JWT
                String jwtToken = token.substring(7);
                String userEmail = jwtUtils.extractUsername(jwtToken);

                // Récupérer la catégorie par ID
                Optional<Categorie> existingCategorieOptional = ICategorieService.retrivecategorie(id);
                if (existingCategorieOptional.isPresent()) {
                    // La catégorie existe, récupérer l'objet Categorie
                    Categorie existingCategorie = existingCategorieOptional.get();

                    // Mettre à jour les champs de la catégorie avec les nouvelles valeurs s'ils sont non null dans la requête
                    if (categorie.getName() != null) {
                        existingCategorie.setName(categorie.getName());
                    }

                    if (categorie.getDescription() != null) {
                        existingCategorie.setDescription(categorie.getDescription());
                    }

                    // Mettre à jour la catégorie dans la base de données
                    Categorie updatedCategorie = ICategorieService.updatecategorie(existingCategorie);

                    response.setStatusCode(200);
                    response.setMessage("Categorie updated successfully.");
                    return ResponseEntity.ok(response);
                } else {
                    // La catégorie n'a pas été trouvée
                    response.setStatusCode(404);
                    response.setMessage("Categorie not found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                // Le jeton est invalide ou manquant
                response.setStatusCode(401);
                response.setMessage("Invalid or missing token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            // Une erreur s'est produite
            response.setStatusCode(500);
            response.setMessage("An error occurred while updating the category: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }





    // Endpoint pour supprimer une catégorie
    @CrossOrigin(origins="http://localhost:4200")
    @DeleteMapping("/delete_categorie/{id}")
    public ResponseEntity<Object> deleteCategorie(@PathVariable Integer id,
                                                  @RequestHeader("Authorization") String token) {
        ReqRes response = new ReqRes();

        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                String userEmail = jwtUtils.extractUsername(jwtToken);

                Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
                if (userOptional.isPresent()) {
                    Users user = userOptional.get();

                    // Vérifiez si l'utilisateur est le propriétaire de la catégorie
                    Optional<Categorie> existingCategorieOptional = ICategorieService.retrivecategorie(id);
                    if (existingCategorieOptional.isPresent()) {
                        Categorie existingCategorie = existingCategorieOptional.get();
                        IBienService.updateBiensCategorieToNull(id);
                        // Supprimez la catégorie de la base de données
                        ICategorieService.removecategorie(id);

                        // Mettre à jour les biens associés pour définir categorie_id à null


                        response.setStatusCode(200);
                        response.setMessage("Categorie deleted successfully.");
                        return ResponseEntity.ok(response);
                    } else {
                        response.setStatusCode(403);
                        response.setMessage("You are not authorized to delete this categorie.");
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                    }

                } else {
                    response.setStatusCode(404);
                    response.setMessage("User not found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                response.setStatusCode(401);
                response.setMessage("Invalid or missing token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("An error occurred while deleting the categorie: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Endpoint pour récupérer toutes les catégories
    @GetMapping("/retrieve_all_categories")
    public ResponseEntity<Object> retrieveAllCategories() {
        try {
            List<Categorie> allCategories = ICategorieService.retriveAllcategorie();
            return ResponseEntity.ok(allCategories);
        } catch (Exception e) {
            ReqRes response = new ReqRes();
            response.setStatusCode(500);
            response.setMessage("An error occurred while retrieving all categories: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/retrieve_biens_by_category_id/{idCategorie}")
    public ResponseEntity<Object> retrieveBiensByCategoryId(@PathVariable Integer idCategorie) {
        try {
            List<Bien> biensByCategoryId = IBienService.retrieveBiensByCategoryId(idCategorie);
            return ResponseEntity.ok(biensByCategoryId);
        } catch (Exception e) {
            ReqRes response = new ReqRes();
            response.setStatusCode(500);
            response.setMessage("An error occurred while retrieving biens by category ID: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @GetMapping("/categorie/get_categorie/{id}")
    public ResponseEntity<Object> getCategoriesById(@PathVariable Integer id,
                                                    @RequestHeader("Authorization") String token) {
        ReqRes response = new ReqRes();

        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                String userEmail = jwtUtils.extractUsername(jwtToken);

                Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
                if (userOptional.isPresent()) {
                    // Vérifiez si l'utilisateur est autorisé à accéder à cette catégorie
                    Optional<Categorie> categorieOptional = ICategorieService.retrivecategorie(id);
                    if (categorieOptional.isPresent()) {
                        // La catégorie existe, récupérer l'objet Categorie
                        Categorie categorie = categorieOptional.get();
                        return ResponseEntity.ok(categorie);
                    } else {
                        // La catégorie n'a pas été trouvée
                        response.setStatusCode(404);
                        response.setMessage("Category not found.");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
                } else {
                    // L'utilisateur n'est pas trouvé
                    response.setStatusCode(404);
                    response.setMessage("User not found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                // Jeton invalide ou manquant
                response.setStatusCode(401);
                response.setMessage("Invalid or missing token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            // Une erreur s'est produite
            response.setStatusCode(500);
            response.setMessage("An error occurred while fetching the category: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }




}




