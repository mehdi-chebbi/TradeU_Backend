package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.entity.Publication;
import com.twd.SpringSecurityJWT.entity.Reponse;
import com.twd.SpringSecurityJWT.entity.Users;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.service.IPublicationService;
import com.twd.SpringSecurityJWT.service.IReponseService;
import com.twd.SpringSecurityJWT.service.JWTUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("ReponseController")
@CrossOrigin(origins = "http://localhost:4200")
public class ReponseController {
    IReponseService reponseService;
    IPublicationService publicationService;
    private JWTUtils jwtUtils;
    private OurUserRepo userRepository;
    @GetMapping("/Get-All-Reponse")
    public List<Reponse> getAllReponse(){return reponseService.getAllReponses();}

    @PostMapping("/add-reponse/{idPublication}")
    public ResponseEntity<?> addReponse(@PathVariable("idPublication") int idPublication, @RequestBody Reponse reponse, Authentication authentication) {
        Users currentUser = (Users) authentication.getPrincipal();

        Publication publication = publicationService.GetPublication(idPublication);
        if (publication == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publication with ID " + idPublication + " doesn't exist");
        }
        if (reponseService.ReponseContainsBadWord(reponse)) {
            return ResponseEntity.ok("{\"message\": \"bad word\"}");
        }

        reponse.setPublication(publication);
        reponse.setReponseCreatedBy(currentUser);
        reponse.setDate_reponse(LocalDate.now());

        reponseService.addReponse(reponse);
        return ResponseEntity.ok("{\"message\": \"reponse added\"}");
    }

    @PutMapping("/update-Reponse/{id}")
    public ResponseEntity<?> updateReponse(@PathVariable int id,@RequestBody Reponse reponse){
        Reponse existingReponse = reponseService.getReponse(id);
        if (existingReponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reponse not found with id: " + id);
        }
        String Reponse_content=reponse.getReponse();
        Reponse updatedReponse=existingReponse;
        updatedReponse.setReponse(Reponse_content);
        reponseService.updateReponse(updatedReponse);
        return ResponseEntity.ok(existingReponse);
    }
    @DeleteMapping("/delete-Reponse/{idRep}")
    public void deleteReponse(@PathVariable("idRep") int idRep ){reponseService.removeReponseById(idRep);}



    @GetMapping("/Translate-Reponse/Ar/{id}")
    public ResponseEntity<String> GetPubByIdsAr(@PathVariable("id") int id){
        Reponse reponse = reponseService.getReponse(id);
        String reponse_content = reponse.getReponse();
        String translatedContent = reponseService.translateReponseToArabic(reponse_content);
        return ResponseEntity.status(HttpStatus.OK).body(translatedContent);

    }
    @GetMapping("/Translate-Reponse/Fr/{id}")
    public ResponseEntity<String> GetPubByIdsFr(@PathVariable("id") int id){
        Reponse reponse = reponseService.getReponse(id);
        String reponse_content = reponse.getReponse();
        String translatedContent = reponseService.translateReponseToFrench(reponse_content);
        return ResponseEntity.status(HttpStatus.OK).body(translatedContent);

    }
    @GetMapping("/Translate-Reponse/Sp/{id}")
    public ResponseEntity<String> GetPubByIdsSp(@PathVariable("id") int id){
        Reponse reponse = reponseService.getReponse(id);
        String reponse_content = reponse.getReponse();
        String translatedContent = reponseService.translateReponseToSpanish(reponse_content);
        return ResponseEntity.status(HttpStatus.OK).body(translatedContent);

    }
@GetMapping("/get-current-user")
public Users getCurrentUser(Authentication authentication){
         Users currentUser = (Users) authentication.getPrincipal();
         return currentUser;

}
    @GetMapping("/statistics/responses")
    public long getNumberOfResponses() {
        return reponseService.getNumberOfResponses();
    }
    @PostMapping("/like_reponse/{id_reponse}")
    public ResponseEntity<?> likeReponse(@PathVariable int id_reponse, @RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Invalid or missing token
            }

            String jwtToken = token.substring(7);
            String userEmail = jwtUtils.extractUsername(jwtToken);

            Optional<Users> userOptional = userRepository.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // User not found or unauthorized
            }

            Users currentUser = userOptional.get();
            Reponse reponse = reponseService.getReponse(id_reponse);

            if (reponse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reponse not found with id: " + id_reponse);
            }

            // Check if the user has already liked the response
            if (reponse.getLikedByUsers().contains(currentUser)) {
                // If the user has already liked the response, remove the like
                reponse.setLikes(reponse.getLikes() - 1);
                reponse.getLikedByUsers().remove(currentUser);
                reponseService.updateReponse(reponse);
                return ResponseEntity.ok("{\"message\": \"Reponse like removed.\"}");
            }

            // Check if the user has previously disliked the response
            if (reponse.getDislikedByUsers().contains(currentUser)) {
                // If the user has previously disliked the response, remove the dislike
                reponse.setDislikes(reponse.getDislikes() - 1);
                reponse.getDislikedByUsers().remove(currentUser);
            }

            // Like the response
            reponse.setLikes(reponse.getLikes() + 1);
            reponse.getLikedByUsers().add(currentUser);
            reponseService.updateReponse(reponse);

            return ResponseEntity.ok("{\"message\": \"Reponse liked successfully.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while processing the request: " + e.getMessage());
        }
    }


    @PostMapping("/dislike_reponse/{id_reponse}")
    public ResponseEntity<?> dislikeReponse(@PathVariable int id_reponse, @RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Invalid or missing token
            }

            String jwtToken = token.substring(7);
            String userEmail = jwtUtils.extractUsername(jwtToken);

            Optional<Users> userOptional = userRepository.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // User not found or unauthorized
            }

            Users currentUser = userOptional.get();
            Reponse reponse = reponseService.getReponse(id_reponse);

            if (reponse == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reponse not found with id: " + id_reponse);
            }

            // Check if the user has already disliked the response
            if (reponse.getDislikedByUsers().contains(currentUser)) {
                // If the user has already disliked the response, remove the dislike
                reponse.setDislikes(reponse.getDislikes() - 1);
                reponse.getDislikedByUsers().remove(currentUser);
                reponseService.updateReponse(reponse);
                return ResponseEntity.ok("{\"message\": \"Reponse dislike removed.\"}");
            }

            // Check if the user has previously liked the response
            if (reponse.getLikedByUsers().contains(currentUser)) {
                // If the user has previously liked the response, remove the like
                reponse.setLikes(reponse.getLikes() - 1);
                reponse.getLikedByUsers().remove(currentUser);
            }

            // Dislike the response
            reponse.setDislikes(reponse.getDislikes() + 1);
            reponse.getDislikedByUsers().add(currentUser);
            reponseService.updateReponse(reponse);

            return ResponseEntity.ok("{\"message\": \"Reponse disliked successfully.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while processing the request: " + e.getMessage());
        }
    }


}