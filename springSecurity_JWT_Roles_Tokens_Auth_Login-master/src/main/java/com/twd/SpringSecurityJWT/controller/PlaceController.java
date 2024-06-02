package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.dto.ReqRes;
import com.twd.SpringSecurityJWT.dto.UserUpdateRequest;
import com.twd.SpringSecurityJWT.email.EmailService;
import com.twd.SpringSecurityJWT.entity.Place;
import com.twd.SpringSecurityJWT.entity.Reservation;
import com.twd.SpringSecurityJWT.entity.UserRole;
import com.twd.SpringSecurityJWT.entity.Users;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.repository.PlaceRepo;
import com.twd.SpringSecurityJWT.repository.ReservationRepo;
import com.twd.SpringSecurityJWT.service.AuthService;
import com.twd.SpringSecurityJWT.service.JWTUtils;
import com.twd.SpringSecurityJWT.service.OurUserDetailsService;
import com.twd.SpringSecurityJWT.service.ReservationService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.util.stream.Collectors;

@EnableScheduling
@Configuration
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class PlaceController {
    @Autowired
    private PlaceRepo placeRepo;
    @Autowired
    private OurUserDetailsService userDetailsService;
    @Autowired
    private AuthService authService;



    private ReservationService reservationService;
    @Autowired
    private OurUserRepo ourUserRepo;

    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private ReservationRepo reservationRepo;
    private EmailService emailService;

    @GetMapping("/public/place")
    public ResponseEntity<Object> getAllPlaces(){
        return ResponseEntity.ok(placeRepo.findAll());
    }

    @GetMapping("/admin/triplace")
    public ResponseEntity<Object> triPlaces() {
        List<Place> places = placeRepo.findAll();

        // Tri des places par la longueur de la description dans l'ordre décroissant
        // puis par ordre alphabétique du nom dans l'ordre croissant
        List<Place> sortedPlaces = places.stream()
                .sorted((place1, place2) -> {
                    // Vérifier les descriptions nulles
                    String description1 = place1.getDescription();
                    String description2 = place2.getDescription();

                    // Comparer les longueurs des descriptions
                    int compare = 0;
                    if (description1 != null && description2 != null) {
                        compare = Integer.compare(description2.length(), description1.length());
                    } else if (description1 == null && description2 != null) {
                        compare = 1; // place1 doit venir après place2
                    } else if (description1 != null) {
                        compare = -1; // place1 doit venir avant place2
                    }

                    if (compare == 0) {
                        // Si les longueurs des descriptions sont égales, trier par ordre alphabétique du nom
                        compare = place1.getName().compareTo(place2.getName());
                    }
                    return compare;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(sortedPlaces);
    }

    @GetMapping("/admin/getPlace/{id}")
    public ResponseEntity<Object> getPlace(@PathVariable Integer id) {
        return ResponseEntity.ok(placeRepo.findById(id));
    }

    @PostMapping("/admin/saveplace")
    public ResponseEntity<Object> saveplace(@RequestBody ReqRes productRequest){
        Place placeToSave = new Place();
        placeToSave.setName(productRequest.getName());
        placeToSave.setDescription(productRequest.getDescription());
        placeToSave.setImageUrl(productRequest.getImageUrl()); // Définir l'URL de l'image
        return ResponseEntity.ok(placeRepo.save(placeToSave));
    }

    @DeleteMapping("/admin/delete/place/{placeId}")
    public ResponseEntity<Object> deletePlace(@PathVariable Integer placeId,
                                              @RequestHeader("Authorization") String token) {
        ReqRes response = new ReqRes();

        try {
            if (token != null && token.startsWith("Bearer ")) {
                // Extraction du token sans le préfixe "Bearer "
                String jwtToken = token.substring(7);

                // Extraction de l'identifiant de l'utilisateur à partir du token
                String userEmail = jwtUtils.extractUsername(jwtToken);

                // Recherche de l'utilisateur dans la base de données par son email
                Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
                if (userOptional.isPresent()) {
                    // Récupération de l'utilisateur
                    Users user = userOptional.get();

                    // Vérification si l'utilisateur est un administrateur
                    if (user.getRole() == UserRole.ADMIN) {
                        // L'utilisateur est un administrateur, autoriser la suppression de la place
                        Optional<Place> placeOptional = placeRepo.findById(placeId);
                        if (placeOptional.isPresent()) {
                            placeRepo.deleteById(placeId);
                            // Réponse avec un message de succès
                            response.setStatusCode(200);
                            response.setMessage("Place deleted successfully.");
                            return ResponseEntity.ok(response);
                        } else {
                            // Si la place n'est pas trouvée, renvoyer une erreur
                            response.setStatusCode(404);
                            response.setMessage("Place not found.");
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                        }
                    } else {
                        // Si l'utilisateur n'est pas un administrateur, renvoyer une erreur 403 Forbidden
                        response.setStatusCode(403);
                        response.setMessage("User is not authorized to delete a place.");
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                    }
                } else {
                    // Si l'utilisateur n'est pas trouvé, renvoyer une erreur
                    response.setStatusCode(404);
                    response.setMessage("User not found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                // Si le token n'est pas fourni ou incorrect, renvoyer une erreur
                response.setStatusCode(401);
                response.setMessage("Invalid or missing token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            // En cas d'erreur, renvoyer une erreur interne du serveur
            response.setStatusCode(500);
            response.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/admin/update/place/{placeId}")
    public ResponseEntity<Object> updatePlace(@PathVariable Integer placeId,
                                              @RequestHeader("Authorization") String token,
                                              @RequestBody Place updateRequest) {
        ReqRes response = new ReqRes();

        try {
            if (token != null && token.startsWith("Bearer ")) {
                // Extraction du token sans le préfixe "Bearer "
                String jwtToken = token.substring(7);

                // Extraction de l'identifiant de l'utilisateur à partir du token
                String userEmail = jwtUtils.extractUsername(jwtToken);

                // Recherche de l'utilisateur dans la base de données par son email
                Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
                if (userOptional.isPresent()) {
                    // Vérification si l'utilisateur est un administrateur
                    Users user = userOptional.get();
                    if (user.getRole() == UserRole.ADMIN) {
                        // Recherche de la place dans la base de données par son ID
                        Optional<Place> placeOptional = placeRepo.findById(placeId);
                        if (placeOptional.isPresent()) {
                            // Mise à jour des données de la place avec les nouvelles valeurs
                            Place place = placeOptional.get();
                            if (updateRequest.getName() != null) {
                                place.setName(updateRequest.getName());
                            }
                            if (updateRequest.getDescription() != null) {
                                place.setDescription(updateRequest.getDescription());
                            }
                            // Enregistrement des modifications dans la base de données
                            placeRepo.save(place);
                            // Réponse avec un message de succès
                            response.setStatusCode(200);
                            response.setMessage("Place updated successfully.");
                            return ResponseEntity.ok(response);
                        } else {
                            // Si la place n'est pas trouvée, renvoyer une erreur
                            response.setStatusCode(404);
                            response.setMessage("Place not found.");
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                        }
                    } else {
                        // Si l'utilisateur n'est pas un administrateur, renvoyer une erreur 403 Forbidden
                        response.setStatusCode(403);
                        response.setMessage("User is not authorized to update a place.");
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                    }
                } else {
                    // Si l'utilisateur n'est pas trouvé, renvoyer une erreur
                    response.setStatusCode(404);
                    response.setMessage("User not found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                // Si le token n'est pas fourni ou incorrect, renvoyer une erreur
                response.setStatusCode(401);
                response.setMessage("Invalid or missing token.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            // En cas d'erreur, renvoyer une erreur interne du serveur
            response.setStatusCode(500);
            response.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}


