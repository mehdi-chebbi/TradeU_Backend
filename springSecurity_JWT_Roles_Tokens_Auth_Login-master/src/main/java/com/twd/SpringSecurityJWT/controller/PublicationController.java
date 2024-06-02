package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.entity.Publication;
import com.twd.SpringSecurityJWT.entity.Users;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.service.IPublicationService;
import com.twd.SpringSecurityJWT.service.JWTUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("PublicationController")
@CrossOrigin(origins = "http://localhost:8083")
public class PublicationController {
    IPublicationService publicationService;
    private JWTUtils jwtUtils;
    private OurUserRepo userRepository;


    @GetMapping("/Get-All-Publication")
    public List<Publication> getAllPublication(){ return publicationService.GetAllPublication(); }


    @PostMapping("/add-publication")
    public ResponseEntity<?> addPublication(@RequestBody Publication publication, Authentication authentication) {

        if (publicationService.publicationContainsBadWord(publication)) {
            return ResponseEntity.ok("{\"message\": \"bad word\"}");
        }
        Users currentUser = (Users) authentication.getPrincipal();
        publication.setLikes(0);
        publication.setDislikes(0);
        publication.setPublicationCreatedBy(currentUser);
        publication.setDatePublication(LocalDateTime.now());

        publicationService.addPublication(publication);
        return ResponseEntity.ok("{\"message\": \"created\"}");
    }
    @PutMapping("/update-Publication/{id}")
    public ResponseEntity<String> updatePublication(
            @PathVariable int id,
            @RequestParam String newcontent) {

        // Retrieve existing publication by id
        Publication existingPublication = publicationService.GetPublication(id);

        if (existingPublication == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Publication not found with id: " + id);
        }

        // Update publication content with new content
        existingPublication.setPublicationContent(newcontent);

        // Save the updated publication
        publicationService.updatePublication(existingPublication);

        return ResponseEntity.ok("Publication updated successfully");
    }

     @DeleteMapping("/Delete-Publication/{idPub}")
    public void removePublication(@PathVariable("idPub") int idPub){ publicationService.removePublicationById(idPub);  }

    @GetMapping("/translate_Publication/ar/{id}")
    public ResponseEntity<String> translateToArabic(@PathVariable("id") int id){
        Publication pub = publicationService.GetPublication(id);
        String pub_content = pub.getPublicationContent();
        String translatedContent = publicationService.translatePubToArabic(pub_content);
        return ResponseEntity.status(HttpStatus.OK).body(translatedContent);

    }
    @GetMapping("/translate_Publication/fr/{id}")
    public ResponseEntity<String> translateToFrench(@PathVariable("id") int id){
        Publication pub = publicationService.GetPublication(id);
        String pub_content = pub.getPublicationContent();
        String translatedContent = publicationService.translatePubToFrench(pub_content);
        return ResponseEntity.status(HttpStatus.OK).body(translatedContent);

    }
    @GetMapping("/translate_Publication/esp/{id}")
    public ResponseEntity<String> translateToSpanish(@PathVariable("id") int id){
        Publication pub = publicationService.GetPublication(id);
        String pub_content = pub.getPublicationContent();
        String translatedContent = publicationService.translatePubToSpanish(pub_content);
        return ResponseEntity.status(HttpStatus.OK).body(translatedContent);

    }

    @GetMapping("/statistics/publications")
    public long getNumberOfPublications() {
        return publicationService.getNumberOfPublications();
    }

    @GetMapping("/tweet/{id}")
    public ResponseEntity<String> tweetPublication(@PathVariable("id") int id) {
        Publication pub = publicationService.GetPublication(id);
        String pubContent = pub.getPublicationContent();

        // Encode publication content for URL
        String encodedPubContent = UriComponentsBuilder.fromUriString(pubContent).build().encode().toString();

        // Construct Twitter Web Intent URL with pre-filled text
        String twitterIntentUrl = "https://twitter.com/intent/tweet?text=" + encodedPubContent;

        // Return the URL as a response to the client
        return ResponseEntity.status(HttpStatus.OK).body(twitterIntentUrl);
    }
    @PostMapping("/like_publication/{id}")
    public ResponseEntity<?> likePublication(@PathVariable int id, @RequestHeader("Authorization") String token) {
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
            Publication publication = publicationService.GetPublication(id);

            if (publication == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publication not found with id: " + id);
            }

            // Check if the user has already liked the publication
            if (publication.getLikedByUsers().contains(currentUser)) {
                // If the user has already liked the publication, remove the like
                publication.setLikes(publication.getLikes() - 1);
                publication.getLikedByUsers().remove(currentUser);
                publicationService.updatePublication(publication);
                return ResponseEntity.ok("{\"message\": \"Publication like removed.\"}");
            }

            // Check if the user has previously disliked the publication
            if (publication.getDislikedByUsers().contains(currentUser)) {
                // If the user has previously disliked the publication, remove the dislike
                publication.setDislikes(publication.getDislikes() - 1);
                publication.getDislikedByUsers().remove(currentUser);
            }

            // Like the publication
            publication.setLikes(publication.getLikes() + 1);
            publication.getLikedByUsers().add(currentUser);
            publicationService.updatePublication(publication);

            return ResponseEntity.ok("{\"message\": \"Publication liked successfully.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while processing the request: " + e.getMessage());
        }
    }



    @PostMapping("/dislike_publication/{id}")
    public ResponseEntity<?> dislikePublication(@PathVariable int id, @RequestHeader("Authorization") String token) {
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
            Publication publication = publicationService.GetPublication(id);

            if (publication == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Publication not found with id: " + id);
            }

            // Check if the user has already disliked the publication
            if (publication.getDislikedByUsers().contains(currentUser)) {
                // If the user has already disliked the publication, remove the dislike
                publication.setDislikes(publication.getDislikes() - 1);
                publication.getDislikedByUsers().remove(currentUser);
                publicationService.updatePublication(publication);
                return ResponseEntity.ok("{\"message\": \"Publication dislike removed.\"}");
            }

            // Check if the user has previously liked the publication
            if (publication.getLikedByUsers().contains(currentUser)) {
                // If the user has previously liked the publication, remove the like
                publication.setLikes(publication.getLikes() - 1);
                publication.getLikedByUsers().remove(currentUser);
            }

            // Dislike the publication
            publication.setDislikes(publication.getDislikes() + 1);
            publication.getDislikedByUsers().add(currentUser);
            publicationService.updatePublication(publication);

            return ResponseEntity.ok("{\"message\": \"Publication disliked successfully.\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while processing the request: " + e.getMessage());
        }
    }




}