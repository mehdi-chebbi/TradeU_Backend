package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.dto.ReqRes;
import com.twd.SpringSecurityJWT.dto.ResetPasswordRequest;
import com.twd.SpringSecurityJWT.dto.UserUpdateRequest;
import com.twd.SpringSecurityJWT.entity.Place;
import com.twd.SpringSecurityJWT.entity.Reservation;
import com.twd.SpringSecurityJWT.entity.Users;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.repository.PlaceRepo;
import com.twd.SpringSecurityJWT.repository.ReservationRepo;
import com.twd.SpringSecurityJWT.service.AuthService;
import com.twd.SpringSecurityJWT.service.JWTUtils;
import com.twd.SpringSecurityJWT.service.OurUserDetailsService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class AdminUsers {

    @Autowired
    private PlaceRepo placeRepo;
    @Autowired
    private OurUserDetailsService userDetailsService;
    @Autowired
    private AuthService authService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private OurUserRepo ourUserRepo;

    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private ReservationRepo reservationRepo;
    private static final String ACCOUNT_SID = "AC98f1c78a7a1bac9c621d4de24cd930a0";
    private static final String AUTH_TOKEN = "5a242f54dc7cac42802eb34ee0954030";




    @GetMapping("/admin/listUsers")
    public ResponseEntity<Object> getAllUsers() {
        return ResponseEntity.ok(ourUserRepo.findAll());
    }





    @PostMapping("/admin/ban/{userId}")
    public ResponseEntity<Object> banUserById(@PathVariable Integer userId) {
        Optional<Users> userOptional = ourUserRepo.findById(userId);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            user.setBanned(true);
            ourUserRepo.save(user);
             sendMessage("You have been banned");
            return ResponseEntity.ok("User with ID " + userId + " has been banned.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/admin/unban/{userId}")
    public ResponseEntity<Object> unbanUserById(@PathVariable Integer userId) {
        Optional<Users> userOptional = ourUserRepo.findById(userId);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            user.setBanned(false);
            ourUserRepo.save(user);
             sendMessage("You have been unbanned");
            return ResponseEntity.ok("User with ID " + userId + " has been banned.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @GetMapping("/user/alone")
    public ResponseEntity<Object> userAlone() {
        return ResponseEntity.ok("USers alone can access this ApI only");
    }

    @GetMapping("/adminuser/both")
    public ResponseEntity<Object> bothAdminaAndUsersApi() {
        return ResponseEntity.ok("Both Admin and Users Can  access the api");
    }

    //reset  password




    /** You can use this to get the details(name,email,role,ip, e.t.c) of user accessing the service*/
    @GetMapping("/public/email")
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication); //get all details(name,email,password,roles e.t.c) of the user
        System.out.println(authentication.getDetails()); // get remote ip
        System.out.println(authentication.getName()); //returns the email because the email is the unique identifier
        return authentication.getName(); // returns the email
    }

//supressionn

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable int userId) {
        // Vérifier si l'utilisateur existe
        if (!ourUserRepo.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Utilisateur avec l'ID " + userId + " non trouvé.");
        }

        // Supprimer l'utilisateur
        ourUserRepo.deleteById(userId);

        return ResponseEntity.ok().body("L'utilisateur avec l'ID " + userId + " a été supprimé avec succès.");
    }


    @PostMapping("/public/profile")
    public ResponseEntity<Object> updateProfile(@RequestHeader("Authorization") String token, @RequestBody UserUpdateRequest userUpdateRequest) {
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
                    // Mise à jour des informations du profil de l'utilisateur
                    Users user = userOptional.get();
                    user.setName(userUpdateRequest.getName());
                    user.setPhone(userUpdateRequest.getPhone());
                    user.setAdress(userUpdateRequest.getAdress());

                    // Enregistrement des modifications dans la base de données
                    ourUserRepo.save(user);

                    // Réponse avec un message de succès
                    response.setStatusCode(200);
                    response.setMessage("User profile updated successfully.");
                    return ResponseEntity.ok(response);
                } else {
                    // Si l'utilisateur n'est pas trouvé, renvoyer une erreur
                    response.setStatusCode(404);
                    response.setMessage("User not found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                // Si le token n'est pas valide, renvoyer une erreur
                response.setStatusCode(400);
                response.setMessage("Invalid token format.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            // En cas d'erreur, renvoyer une erreur interne du serveur
            response.setStatusCode(500);
            response.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    private void sendMessage(String message) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message.creator(
                        new com.twilio.type.PhoneNumber("+21692142447"),
                        new com.twilio.type.PhoneNumber("+12512378681"),
                        message)
                .create();

    }

    @PostMapping("/public/logout")
    public ResponseEntity<Object> logout(@RequestHeader("Authorization") String tokenHeader) {
        ReqRes response = new ReqRes();

        try {
            // Vérification si le token existe dans l'en-tête Authorization
            if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
                // Extraction du token sans le préfixe "Bearer "
                String jwtToken = tokenHeader.substring(7);

                // Extraction de l'identifiant de l'utilisateur à partir du token
                String userEmail = jwtUtils.extractUsername(jwtToken);

                // Recherche de l'utilisateur dans la base de données par son email
                Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);

                if (userOptional.isPresent()) {
                    // Suppression du token de l'utilisateur
                    Users user = userOptional.get();

                    //    user.setToken(null);
                    user.setOnline(false);
                    //  user.setToken("null");
                    ourUserRepo.save(user);

                    // Réponse avec un message de déconnexion réussie
                    response.setStatusCode(200);
                    response.setMessage("User logged out successfully.");
                    return ResponseEntity.ok(response);
                } else {
                    // Si l'utilisateur n'est pas trouvé, renvoyer une erreur
                    response.setStatusCode(404);
                    response.setMessage("User not found.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                }
            } else {
                // Si le token est invalide, renvoyer une erreur
                response.setStatusCode(400);
                response.setMessage("Invalid token format.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            // En cas d'erreur, renvoyer une erreur interne du serveur
            response.setStatusCode(500);
            response.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }



    @PostMapping("/auth/envoicode")
    public ResponseEntity<Object> sendVerificationCode(@RequestBody Map<String, String> requestBody) {
        String email = requestBody.get("email");
        if (email == null) {
            return ResponseEntity.badRequest().body("L'e-mail est manquant dans le corps de la requête JSON.");
        }

        // Récupérez l'utilisateur à partir de l'e-mail
        Optional<Users> user = ourUserRepo.findByEmail(email);
        if (!user.isPresent()) {
            return ResponseEntity.badRequest().body("Utilisateur non trouvé pour l'e-mail spécifié.");
        }
        String code = generateRandomCode();
        sendMessage(code);
        user.get().setVerificationCode(code);
        ourUserRepo.save(user.get());



        // Envoyez le code de vérification via SMS avec Twilio

        return ResponseEntity.ok("Code de vérification envoyé avec succès.");
    }


    @PostMapping("/auth/resetpassword")
    public ResponseEntity<Object> resetPassword(@RequestBody ResetPasswordRequest request) {
        // Récupérez l'utilisateur à partir de l'e-mail
        Optional<Users> userOptional = ourUserRepo.findByEmail(request.getEmail());
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Utilisateur non trouvé pour l'e-mail spécifié.");
        }
        Users user = userOptional.get();

        // Vérifiez si le code de vérification est correct
        if (!request.getCode().equals(user.getVerificationCode())) {
            return ResponseEntity.badRequest().body("Le code de vérification est incorrect.");
        }

        // Mettre à jour le mot de passe de l'utilisateur
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setVerificationCode(null); // Réinitialiser le code de vérification après utilisation
        ourUserRepo.save(user);

        return ResponseEntity.ok("Mot de passe réinitialisé avec succès.");
    }




    private String generateRandomCode() {
        // Générez un code de 4 chiffres aléatoire
        Random random = new Random();
        int code = random.nextInt(9000) + 1000; // Génère un nombre aléatoire entre 1000 et 9999
        return String.valueOf(code);
    }





}

