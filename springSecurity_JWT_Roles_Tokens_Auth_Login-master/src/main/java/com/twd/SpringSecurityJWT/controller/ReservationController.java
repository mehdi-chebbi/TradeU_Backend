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
import lombok.AllArgsConstructor;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;

@EnableScheduling
@Configuration
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class ReservationController {
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

    public ReservationController(EmailService emailService, OurUserRepo ourUserRepo, PlaceRepo placeRepo, ReservationRepo reservationRepo, JWTUtils jwtUtils, ReservationService reservationService) {
        this.emailService = emailService;
        this.ourUserRepo = ourUserRepo;
        this.placeRepo = placeRepo;
        this.reservationRepo = reservationRepo;
        this.jwtUtils = jwtUtils;
        this.reservationService = reservationService;
    }



    @GetMapping("/statistics/reservations")
    public ResponseEntity<Map<Place, Long>> getReservationStatistics() {
        Map<Place, Long> reservationStatistics = reservationService.getReservationCountByPlace();
        return ResponseEntity.ok(reservationStatistics);
    }
    @PostMapping("/user/add-reservation1")
    public ResponseEntity<Object> addReservation1(@RequestHeader("Authorization") String token,
                                                  @RequestBody Map<String, Object> requestBody) {
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
                    Users user = userOptional.get();

                    // Vérifier si le corps de la requête contient la clé "placeId"
                    if (requestBody.containsKey("placeId")) {
                        // Récupérer la valeur de "placeId" depuis le corps de la requête
                        Integer placeId = Integer.parseInt(requestBody.get("placeId").toString());

                        // Recherche de la place dans la base de données par son ID
                        Optional<Place> placeOptional = placeRepo.findById(placeId);
                        if (placeOptional.isPresent()) {
                            Place place = placeOptional.get();

                            // Créer et configurer la réservation
                            Reservation reservation = new Reservation();
                            reservation.setUser(user);
                            reservation.setPlace(place);

                            // Conversion de la date
                            String dateString = requestBody.get("reservationDate").toString();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                            LocalDate localDate = LocalDate.parse(dateString, formatter);
                            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                            reservation.setReservationDate(date);


                            // Assigner les autres propriétés de la réservation depuis le corps de la requête
                            reservation.setHeureDebut(LocalTime.parse(requestBody.get("heureDebut").toString()));
                            reservation.setHeureFin(LocalTime.parse(requestBody.get("heureFin").toString()));
                            reservation.setDescription(requestBody.get("description").toString());

                            // Marquer la place comme réservée
                            place.setReserved(true);

                            // Enregistrement de la réservation et de la mise à jour de la place dans la base de données
                            reservationRepo.save(reservation);
                            placeRepo.save(place);

                            // Envoyer un e-mail de confirmation
                            String emailBody = "Réservation effectuée avec succès.\n" +
                                    "Date: " + reservation.getReservationDate() + "\n" +
                                    "Heure de début: " + reservation.getHeureDebut() + "\n" +
                                    "Heure de fin: " + reservation.getHeureFin() + "\n" +
                                    "Description: " + reservation.getDescription();
                            emailService.send(userEmail, emailBody);

                            // Réponse avec un message de succès
                            response.setStatusCode(200);
                            response.setMessage("Reservation added successfully.");
                            return ResponseEntity.ok(response);
                        } else {
                            // Si la place n'est pas trouvée, renvoyer une erreur
                            response.setStatusCode(404);
                            response.setMessage("Place not found.");
                            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                        }
                    } else {
                        // Si "placeId" n'est pas fourni dans le corps de la requête, renvoyer une erreur
                        response.setStatusCode(400);
                        response.setMessage("Missing 'placeId' in request body.");
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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


    @GetMapping("/user/listReservation")
    public ResponseEntity<Object> getAllReservations() {
        List<Reservation> reservations = reservationRepo.findAll();
        List<Object> response = new ArrayList<>();

        for (Reservation reservation : reservations) {
            Map<String, Object> reservationInfo = new HashMap<>();
            reservationInfo.put("id", reservation.getId());
            reservationInfo.put("reservationDate", reservation.getReservationDate());
            reservationInfo.put("heureDebut", reservation.getHeureDebut());
            reservationInfo.put("heureFin", reservation.getHeureFin());
            reservationInfo.put("description", reservation.getDescription());

            // Ajouter les informations de la place
            reservationInfo.put("placeId", reservation.getPlace().getId());
            reservationInfo.put("placeName", reservation.getPlace().getName()); // Suppose que le nom de la place est "nom"

            response.add(reservationInfo);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/add-reservation")
    public ResponseEntity<Object> addReservation(@RequestHeader("Authorization") String token,
                                                 @RequestParam Integer placeId,
                                                 @RequestParam String reservationDate,
                                                 @RequestParam String heureDebut,
                                                 @RequestParam String heureFin,
                                                 @RequestParam String description) {
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
                    // Recherche de la place dans la base de données par son ID
                    Optional<Place> placeOptional = placeRepo.findById(placeId);
                    if (placeOptional.isPresent()) {
                        Place place = placeOptional.get();
                        // Vérifier si la place est déjà réservée
                        if (place.isReserved()) {
                            // Si la place est déjà réservée, renvoyer une erreur
                            response.setStatusCode(400);
                            response.setMessage("Place is already reserved.");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                        }

                        // Création de la réservation
                        Reservation reservation = new Reservation();
                        reservation.setUser(userOptional.get());
                        reservation.setPlace(place);


                        // Parsing de la date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = dateFormat.parse(reservationDate);
                        reservation.setReservationDate(date);
                        reservation.setHeureDebut(LocalTime.parse(heureDebut));
                        reservation.setHeureFin(LocalTime.parse(heureFin));
                        reservation.setDescription(description);

                        // Marquer la place comme réservée
                        place.setReserved(true);

                        // Enregistrement de la réservation et de la mise à jour de la place dans la base de données
                        reservationRepo.save(reservation);
                        placeRepo.save(place);

                        // Réponse avec un message de succès
                        response.setStatusCode(200);
                        response.setMessage("Reservation added successfully.");
                        return ResponseEntity.ok(response);
                    } else {
                        // Si la place n'est pas trouvée, renvoyer une erreur
                        response.setStatusCode(404);
                        response.setMessage("Place not found.");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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
        } catch (ParseException e) {
            // En cas d'erreur de parsing de la date, renvoyer une erreur
            response.setStatusCode(400);
            response.setMessage("Invalid date format. Please provide the date in yyyy-MM-dd format.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            // En cas d'erreur, renvoyer une erreur interne du serveur
            response.setStatusCode(500);
            response.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/user/delete/reservation/{reservationId}")
    public ResponseEntity<Object> deleteReservation(@PathVariable Integer reservationId,
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
                    // Recherche de la réservation dans la base de données par son ID
                    Optional<Reservation> reservationOptional = reservationRepo.findById(reservationId);
                    if (reservationOptional.isPresent()) {
                        Reservation reservation = reservationOptional.get();
                        Users user = userOptional.get();
                        // Vérifie si l'utilisateur associé au token est également l'utilisateur qui a créé la réservation
                        if (reservation.getUser().getId().equals(user.getId())) {
                            // Obtenez la place associée à la réservation
                            Place place = reservation.getPlace();
                            // Mettre à jour l'attribut isReserved de la place à false
                            place.setReserved(false);
                            // Enregistrez les modifications de la place dans la base de données
                            placeRepo.save(place);
                            // Supprimer la réservation de la base de données
                            reservationRepo.deleteById(reservationId);
                            // Réponse avec un message de succès
                            response.setStatusCode(200);
                            response.setMessage("Reservation deleted successfully.");
                            return ResponseEntity.ok(response);
                        } else {
                            // Si l'utilisateur n'est pas autorisé à supprimer cette réservation, renvoyer une erreur
                            response.setStatusCode(403);
                            response.setMessage("User is not authorized to delete this reservation.");
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                        }
                    } else {
                        // Si la réservation n'est pas trouvée, renvoyer une erreur
                        response.setStatusCode(404);
                        response.setMessage("Reservation not found.");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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

    @PostMapping("/user/update/reservation/{reservationId}")
    public ResponseEntity<Object> updateReservation(@PathVariable Integer reservationId,
                                                    @RequestHeader("Authorization") String token,
                                                    @RequestBody Reservation updatedReservation) {
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
                    // Recherche de la réservation dans la base de données par son ID
                    Optional<Reservation> reservationOptional = reservationRepo.findById(reservationId);
                    if (reservationOptional.isPresent()) {
                        Reservation reservation = reservationOptional.get();
                        Users user = userOptional.get();
                        // Vérifie si l'utilisateur associé au token est également l'utilisateur qui a créé la réservation
                        if (reservation.getUser().getId().equals(user.getId())) {
                            // Mettre à jour la réservation avec les nouvelles données
                            reservation.setReservationDate(updatedReservation.getReservationDate());
                            reservation.setHeureDebut(updatedReservation.getHeureDebut());
                            reservation.setHeureFin(updatedReservation.getHeureFin());
                            reservation.setDescription(updatedReservation.getDescription());
                            // Enregistrement de la réservation mise à jour dans la base de données
                            reservationRepo.save(reservation);
                            // Réponse avec un message de succès
                            response.setStatusCode(200);
                            response.setMessage("Reservation updated successfully.");
                            return ResponseEntity.ok(response);
                        } else {
                            // Si l'utilisateur n'est pas autorisé à modifier cette réservation, renvoyer une erreur
                            response.setStatusCode(403);
                            response.setMessage("User is not authorized to update this reservation.");
                            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                        }
                    } else {
                        // Si la réservation n'est pas trouvée, renvoyer une erreur
                        response.setStatusCode(404);
                        response.setMessage("Reservation not found.");
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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

    @GetMapping("/search")
    public List<Reservation> searchReservations(@RequestParam(required = false) Integer userId,
                                                @RequestParam(required = false) Integer placeId,
                                                @RequestParam(required = false) Date reservationDate,
                                                @RequestParam(required = false) String description,
                                                @RequestParam(required = false) LocalTime heureDebut,
                                                @RequestParam(required = false) LocalTime heureFin) {
        return reservationRepo.findByCriteria(userId, placeId, reservationDate, description, heureDebut, heureFin);
    }

    @GetMapping("/user/reservation-history")
    public ResponseEntity<List<Reservation>> getUserReservationHistory(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                // Extraction du token sans le préfixe "Bearer "
                String jwtToken = token.substring(7);

                // Extraction de l'identifiant de l'utilisateur à partir du token
                String userEmail = jwtUtils.extractUsername(jwtToken);

                // Recherche de l'utilisateur dans la base de données par son email
                Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
                if (userOptional.isPresent()) {
                    // Récupération de l'historique des réservations de l'utilisateur depuis la base de données
                    List<Reservation> reservationHistory = reservationRepo.findByUser(userOptional.get());

                    // Retourner l'historique des réservations avec un code de statut 200 OK
                    return ResponseEntity.ok(reservationHistory);
                } else {
                    // Si l'utilisateur n'est pas trouvé, renvoyer une erreur
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                }
            } else {
                // Si le token n'est pas fourni ou incorrect, renvoyer une erreur
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (Exception e) {
            // En cas d'erreur, renvoyer une erreur interne du serveur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/getReservation/{reservationId}")
    public ResponseEntity<Object> getReservation(@PathVariable Integer reservationId) {
        return ResponseEntity.ok(reservationRepo.findById(reservationId));
    }


}

