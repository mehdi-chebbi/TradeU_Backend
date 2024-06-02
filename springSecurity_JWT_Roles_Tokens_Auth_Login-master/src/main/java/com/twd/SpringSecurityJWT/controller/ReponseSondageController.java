package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.entity.Question;
import com.twd.SpringSecurityJWT.entity.ReponseSondage;
import com.twd.SpringSecurityJWT.entity.Users;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.repository.QuestionRepo;
import com.twd.SpringSecurityJWT.repository.ReponseRepo;
import com.twd.SpringSecurityJWT.service.IReponseSondageService;
import com.twd.SpringSecurityJWT.service.JWTUtils;
import io.jsonwebtoken.MalformedJwtException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("ReponseSonController")
public class ReponseSondageController {
     IReponseSondageService reponseService;
        OurUserRepo ourUserRepo;
    private JWTUtils jwtUtils;
    ReponseRepo reponseRepo;
    QuestionRepo qr;

    @GetMapping("/retrieve-All-Reponse")
    public List<ReponseSondage> GetAllReponse(){
        return reponseService.retrieveAllReponse();
    }
    @PostMapping("/add-reponse/{questionId}")
    public ResponseEntity<ReponseSondage> addReponse(@RequestHeader("Authorization") String token,
                                                     @RequestBody ReponseSondage reponseSondage,
                                                     @PathVariable Integer questionId) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Invalid or missing token
            }

            String jwtToken = token.substring(7);
            String userEmail = jwtUtils.extractUsername(jwtToken);
            Question question = qr.findQuestionById(questionId);

            Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // User not found or unauthorized
            }

            Users user = userOptional.get();

            reponseSondage.setQuestion(question); // Set the question
            reponseSondage.setUser(user); // Set the user

            ReponseSondage addedReponse = reponseService.addReponse(reponseSondage);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedReponse);
        } catch (MalformedJwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




    @PutMapping("/update-reponse")
    public ReponseSondage updateReponse(@RequestBody ReponseSondage reponseSondage){return reponseService.updateReponse(reponseSondage);}
    @DeleteMapping("/{id-reponse}/delete-Reponse")
    public void removeReponse(@PathVariable("id-reponse") Integer idReponse){
        reponseService.removeReponse(idReponse);
    }
    @PostMapping("/add-list-Reponse/{questionId}")
    public ResponseEntity<List<ReponseSondage>> addListReponse(@RequestHeader("Authorization") String token,
                                                               @RequestBody List<ReponseSondage> reponses,
                                                               @PathVariable Integer questionId) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Invalid or missing token
            }

            String jwtToken = token.substring(7);
            String userEmail = jwtUtils.extractUsername(jwtToken);
            Question question = qr.findQuestionById(questionId);

            Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // User not found or unauthorized
            }

            Users user = userOptional.get();

            // Set the question_id for all reponses in the list
            for (ReponseSondage reponse : reponses) {
                reponse.setQuestion(question);
                reponse.setUser(user);
            }

            List<ReponseSondage> addedReponses = reponseService.addListReponse(reponses);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedReponses);
        } catch (MalformedJwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/retrieve-Reponse-byIdQuestion/{id-question}")
    public List<ReponseSondage> GetReponseByIdQuestion(@PathVariable("id-question") Integer idQuestion){
        return reponseRepo.findReponseSondageByQuestion_Id(idQuestion);

    }




}
