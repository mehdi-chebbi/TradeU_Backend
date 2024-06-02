package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.entity.Feedback;
import com.twd.SpringSecurityJWT.entity.FeedbackBien;
import com.twd.SpringSecurityJWT.entity.Users;
import com.twd.SpringSecurityJWT.repository.FeedbackBienRepo;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.service.IFeedBackBienService;
import com.twd.SpringSecurityJWT.service.IFeedBackService;
import com.twd.SpringSecurityJWT.service.JWTUtils;
import io.jsonwebtoken.MalformedJwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:8083")
@RequestMapping("FeedbackBienController")
public class FeedBackBienController {
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    IFeedBackBienService fbbService;
    @Autowired
    private OurUserRepo ourUserRepo;
    @Autowired
    private FeedbackBienRepo fbrepo;

    @PostMapping("/add-feedbackbien")
    public ResponseEntity<?> addFeedback(@RequestHeader("Authorization") String token,
                                         @RequestBody FeedbackBien feedbackBien) {
        try {
            log.info("Received request to add feedbackBien: {}", feedbackBien);

            if (token == null || !token.startsWith("Bearer ")) {
                log.warn("Unauthorized request. Token is null or invalid.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String jwtToken = token.substring(7);
            String userEmail = jwtUtils.extractUsername(jwtToken);

            log.info("User email extracted from token: {}", userEmail);

            Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                log.warn("User with email {} not found.", userEmail);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Users user = userOptional.get();
            feedbackBien.setUser(user);

            // Set the submission date to the current time
            feedbackBien.setSubmissionDate2(new Date());

            log.info("Adding feedbackBien: {}", feedbackBien);
            FeedbackBien createdFeedbackBien = fbbService.addFeedbackBien(feedbackBien);
            log.info("FeedbackBien added successfully: {}", createdFeedbackBien);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdFeedbackBien);
        } catch (MalformedJwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Invalid JWT token
        } catch (Exception e) {
            log.error("Internal server error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Internal server error
        }
    }
    @GetMapping("/retrieve-all")
    public List<FeedbackBien> getAllFeedback(){
        return fbbService.retrieveAllFeedbackBien();
    }
    @DeleteMapping("/{id-feedbackb}/delete-feedback")
    public void removeFeedback(@PathVariable("id-feedbackb") Integer idFeedbackb){
        fbbService.removeFeedbackBien(idFeedbackb);
    }
    @GetMapping("/retrieve-feedback-byBienId/{id-bien}")
    public List<FeedbackBien> getFeedbacksByBienId(@PathVariable("id-bien") Integer idBien){
        return fbrepo.findFeedbackBienByBienId(idBien);
    }
}
