package com.twd.SpringSecurityJWT.controller;
import com.twd.SpringSecurityJWT.entity.Bien;
import com.twd.SpringSecurityJWT.repository.BienRepo;
import com.twd.SpringSecurityJWT.repository.CategorieRepo;
import com.twd.SpringSecurityJWT.service.AuthService;
import com.twd.SpringSecurityJWT.service.CloudinaryService;
import com.twd.SpringSecurityJWT.service.IBienService;
import com.twd.SpringSecurityJWT.service.JWTUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import com.twd.SpringSecurityJWT.dto.ReqRes;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.entity.Users;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;


import java.time.LocalDate;
import java.util.*;


@RestController
@Slf4j
@AllArgsConstructor
@EnableScheduling
@Configuration
@CrossOrigin(origins="http://localhost:4200")
//@RequestMapping("/user/bien")
public class BienController {
    @Autowired
    private AuthService authService;
    BienRepo BienRepo;

    @Autowired
    private OurUserRepo ourUserRepo;
    private CategorieRepo categorieRepo;

    @Autowired
    private JWTUtils jwtUtils;
    IBienService IBienService;


    @Autowired
    private CloudinaryService cloudinaryService;


    @PostMapping(value = "/user/bien/add_bien", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> addBien(@RequestHeader("Authorization") String token,
                                          @ModelAttribute Bien bien) {
        ReqRes response = new ReqRes(); // Assurez-vous que ReqRes est une classe correctement définie pour gérer les réponses

        try {
            // Vérifier si le token est valide
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                String userEmail = jwtUtils.extractUsername(jwtToken);

                // Vérifier si l'utilisateur existe
                Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
                if (userOptional.isPresent()) {
                    Users user = userOptional.get();
                    bien.setUser(user); // Associez l'utilisateur au bien
                    bien.setDateAjout(LocalDate.now());

                    // Vérifier si l'utilisateur a atteint le nombre maximal d'essais pour ajouter un bien avec des mots interdits
                    if (user.getNombreBienAvecBadWord() >= 5) {
                        response.setStatusCode(400);
                        response.setMessage("The user has exceeded the maximum number of attempts to add a property with forbidden words");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }

                    // Vérifier la description du bien pour les mots interdits
                    if (contientMotsInterdits(bien.getDiscription())) {
                        // Incrémenter le nombre de biens contenant des mots interdits pour cet utilisateur
                        user.setNombreBienAvecBadWord(user.getNombreBienAvecBadWord() + 1);
                        // Mettre à jour l'utilisateur dans la base de données
                        ourUserRepo.save(user);
                        response.setStatusCode(400);
                        response.setMessage("The description contains forbidden words");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                    }

                    // Enregistrement de l'image sur Cloudinary
                    String imageUrl = (String) cloudinaryService.upload(bien.getFile()).get("url");

                    // Ajouter le bien
                    bien.setImageUrl(imageUrl); // Définir l'URL de l'image
                    Bien createdBien = IBienService.addbien(bien);

                    response.setStatusCode(200);
                    response.setMessage("Bien ajouté avec succès.");
                    return ResponseEntity.ok(response);
                } else {
                    response.setStatusCode(404);
                    response.setMessage("Utilisateur non trouvé.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                response.setStatusCode(401);
                response.setMessage("Jeton invalide ou manquant.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Une erreur s'est produite lors de l'ajout du bien : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/get-all-bien")
    public ResponseEntity<List<Bien>> getAllBiens() {
        List<Bien> biens = IBienService.getAllBiens();
        return ResponseEntity.ok(biens);
    }


















    // Méthode pour vérifier si la description contient des mots interdits
    private boolean contientMotsInterdits(String description) {
        try {
            // Lire tous les mots interdits depuis le fichier
            List<String> motsInterdits = Files.readAllLines(Paths.get("C:\\Users\\Khalil\\Desktop\\pi integree\\PiCloud-main\\springSecurity_JWT\\springSecurity_JWT_Roles_Tokens_Auth_Login-master\\src\\main\\resources\\mots_interdits.txt"));

            // Vérifie si la description contient des mots interdits
            for (String mot : motsInterdits) {
                if (description.toLowerCase().contains(mot.toLowerCase())) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier de mots interdits : " + e.getMessage());
            // En cas d'erreur, on considère que la description ne contient pas de mots interdits
            return false;
        }
    }

    @Scheduled(cron = "0 41 13 * * ?") // Cette annotation déclenche la méthode tous les jours à minuit
    public void resetBadWordCounters() {
        // Récupérez tous les utilisateurs
        List<Users> users = ourUserRepo.findAll();

        // Réinitialisez le compteur pour chaque utilisateur
        for (Users user : users) {
            user.setNombreBienAvecBadWord(0);
            ourUserRepo.save(user);
        }
    }




    @PostMapping(value = "/user/bien/update_bien/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> updateBien(@PathVariable Integer id,
                                             @RequestHeader("Authorization") String token,
                                             @ModelAttribute Bien bien) {
        ReqRes response = new ReqRes(); // Assurez-vous que ReqRes est une classe correctement définie pour gérer les réponses

        try {
            // Vérifier si le token est valide
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                String userEmail = jwtUtils.extractUsername(jwtToken);

                // Vérifier si l'utilisateur existe
                Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
                if (userOptional.isPresent()) {
                    Users user = userOptional.get();

                    // Vérifier si l'utilisateur est autorisé à mettre à jour le bien
                    Optional<Bien> existingBienOptional = IBienService.retrivebien(id);
                    if (existingBienOptional.isPresent()) {
                        Bien existingBien = existingBienOptional.get();

                        // Vérifier si l'utilisateur est le propriétaire du bien
                        if (existingBien.getUser() != null && existingBien.getUser().getId().equals(user.getId())) {
                            // Mettre à jour les champs du bien avec les nouvelles valeurs s'ils sont non null dans la requête
                            if (bien.getNom() != null) {
                                existingBien.setNom(bien.getNom());
                            }
                            if (bien.getDiscription() != null) {
                                existingBien.setDiscription(bien.getDiscription());
                            }
                            if (bien.getPrix() != null) {
                                existingBien.setPrix(bien.getPrix());
                            }
                            if (bien.getDateAjout() != null) {
                                existingBien.setDateAjout(bien.getDateAjout());
                            }
                            if (bien.getCategorie() != null) {
                                existingBien.setCategorie(bien.getCategorie());
                            }
                            if (bien.getFile() != null) {
                                String imageUrl = (String) cloudinaryService.upload(bien.getFile()).get("url");
                                existingBien.setImageUrl(imageUrl);
                            }


                            // Mettre à jour le bien dans la base de données
                            Bien updatedBien = IBienService.updatebien(existingBien);

                            response.setStatusCode(200);
                            response.setMessage("Bien mis à jour avec succès.");
                            return ResponseEntity.ok(response);
                        } else {
                            response.setStatusCode(403);
                            response.setMessage("Vous n'êtes pas autorisé à mettre à jour ce bien.");
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                        }
                    } else {
                        response.setStatusCode(404);
                        response.setMessage("Bien non trouvé.");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                    }
                } else {
                    response.setStatusCode(404);
                    response.setMessage("Utilisateur non trouvé.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                response.setStatusCode(401);
                response.setMessage("Jeton invalide ou manquant.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Une erreur s'est produite lors de la mise à jour du bien : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    // Méthode pour supprimer un bien
    @DeleteMapping("/user/bien/delete_bien/{id}")
    public ResponseEntity<Object> deleteBien(@PathVariable Integer id,
                                             @RequestHeader("Authorization") String token) {
        ReqRes response = new ReqRes();

        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                String userEmail = jwtUtils.extractUsername(jwtToken);

                Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
                if (userOptional.isPresent()) {
                    Users user = userOptional.get();

                    // Vérifiez si l'utilisateur est le propriétaire du bien
                    Optional<Bien> existingBienOptional = IBienService.retrivebien(id);
                    if (existingBienOptional.isPresent()) {
                        Bien existingBien = existingBienOptional.get();
                        if (existingBien.getUser() != null && existingBien.getUser().getId().equals(user.getId())) {
                            // Supprimez le bien de la base de données
                            IBienService.removebien(id);

                            response.setStatusCode(200);
                            response.setMessage("Bien deleted successfully.");
                            return ResponseEntity.ok(response);
                        } else {
                            response.setStatusCode(403);
                            response.setMessage("You are not authorized to delete this bien.");
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                        }
                    } else {
                        response.setStatusCode(404);
                        response.setMessage("Bien not found.");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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
            response.setMessage("An error occurred while deleting the bien: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



















    //////////////////////////
    @CrossOrigin(origins="http://localhost:4200")
    @DeleteMapping("/admin/bien/delete_bien_admin/{id}")
    public ResponseEntity<Object> deleteBienAdmin(@PathVariable Integer id,
                                                  @RequestHeader("Authorization") String token) {
        ReqRes response = new ReqRes();

        try {
            if (token != null && token.startsWith("Bearer ")) {
                // Vérification du token et extraction de l'e-mail de l'utilisateur
                String jwtToken = token.substring(7);
                String userEmail = jwtUtils.extractUsername(jwtToken);

                // Vérification si l'utilisateur existe
                Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
                if (userOptional.isPresent()) {
                    // Suppression du bien de la base de données
                    IBienService.removebien(id);

                    response.setStatusCode(200);
                    response.setMessage("Bien deleted successfully.");
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
            response.setMessage("An error occurred while deleting the bien: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/admin/bien/retriveAllbien")
    public ResponseEntity<Object> retrieveAllBien(){
        return ResponseEntity.ok(IBienService.retriveAllBien());

}

    @GetMapping("/user/bien/retriveAllbien")
    public ResponseEntity<Object> retrieveAllAuthorizedBien() {
        return ResponseEntity.ok(IBienService.retrieveAllAuthorizedBien());
    }

    @CrossOrigin(origins="http://localhost:4200")
    @PutMapping("/admin/bien/{id}/autorisation")
    public ResponseEntity<Object> changerEtatAutorisationBien(@PathVariable Integer id,
                                                              @RequestParam boolean autorise) {
        try {
            IBienService.changerEtatAutorisationBien(id, autorise);
            return ResponseEntity.ok("The authorization status of the property with ID \" + id + \" has been successfully updated");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue : " + e.getMessage());
        }
    }





    @PutMapping("/admin/bien/autorisation/{id}")
    public ResponseEntity<Object> changerEtatAutorisationBien(@PathVariable Integer id) {
        try {
            // Trouver le bien correspondant à l'ID fourni
            Bien bien = BienRepo.findById(id).get();
            if (bien == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Le bien avec l'ID " + id + " n'existe pas.");
            }

            // Modifier l'état de l'attribut authorize du bien à true
            bien.setAutorise(true);
            BienRepo.save(bien);

            return ResponseEntity.ok("L'état d'autorisation du bien avec l'ID " + id + " a été modifié avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue : " + e.getMessage());
        }
    }



    @PutMapping("/admin/bien/notautorisation/{id}")
    public ResponseEntity<Object> changerEtatAutorisationBienfalse(@PathVariable Integer id) {
        try {
            // Trouver le bien correspondant à l'ID fourni
            Bien bien = BienRepo.findById(id).get();
            if (bien == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Le bien avec l'ID " + id + " n'existe pas.");
            }

            // Modifier l'état de l'attribut authorize du bien à true
            bien.setAutorise(false);
            BienRepo.save(bien);

            return ResponseEntity.ok("The authorization status of the property with ID \" + id + \" has been successfully updated.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur est survenue : " + e.getMessage());
        }
    }



    @GetMapping("/user/bien/get_bien/{id}")
    public ResponseEntity<Object> getBienById(@PathVariable Integer id,
                                              @RequestHeader("Authorization") String token) {
        ReqRes response = new ReqRes();

        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                String userEmail = jwtUtils.extractUsername(jwtToken);

                Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
                if (userOptional.isPresent()) {
                    Users user = userOptional.get();

                    // Vérifiez si l'utilisateur est autorisé à accéder à ce bien
                    Optional<Bien> existingBienOptional = IBienService.retrivebien(id);
                    if (existingBienOptional.isPresent()) {
                        Bien existingBien = existingBienOptional.get();
                        if (existingBien.getUser() != null && existingBien.getUser().getId().equals(user.getId())) {
                            // L'utilisateur est autorisé à accéder à ce bien
                            return ResponseEntity.ok(existingBien);
                        } else {
                            response.setStatusCode(403);
                            response.setMessage("You are not authorized to access this bien.");
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                        }
                    } else {
                        response.setStatusCode(404);
                        response.setMessage("Bien not found.");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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
            response.setMessage("An error occurred while fetching the bien: " + e.getMessage());
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
    @PatchMapping("/user/bien/update-badfeedcountbien/{bienId}")
    public ResponseEntity<?> updateBadFeedCount(@PathVariable("bienId") Integer bienId) {
        try {
            IBienService.updateBadFeedCount(bienId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update badfeedcount: " + e.getMessage());
        }
    }

}
