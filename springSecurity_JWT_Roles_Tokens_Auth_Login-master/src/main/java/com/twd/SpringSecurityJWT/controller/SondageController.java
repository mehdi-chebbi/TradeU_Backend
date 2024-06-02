package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.dto.SondageQuestionDTO;
import com.twd.SpringSecurityJWT.entity.Sondage;
import com.twd.SpringSecurityJWT.entity.Users;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.repository.SondageRepo;
import com.twd.SpringSecurityJWT.service.ISondageService;
import com.twd.SpringSecurityJWT.service.JWTUtils;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("Sondagecontroller")
@CrossOrigin(origins = "http://localhost:4200")
public class SondageController {
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
            private OurUserRepo ourUserRepo;

ISondageService sondageService;
SondageRepo sondageRepo;
    @GetMapping("/retrieve-All-Sondage")
    public List<Sondage> getAllSondage(){
        return sondageService.retrieveAllSondage();
    }
    @PostMapping("/add-sondage")
    public ResponseEntity<?> addSondage(@RequestHeader("Authorization") String token,
                                        @RequestBody Sondage sondage) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Invalid or missing token
            }

            String jwtToken = token.substring(7);
            String userEmail = jwtUtils.extractUsername(jwtToken);

            Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // User not found or unauthorized
            }


            Users user = userOptional.get();
            sondage.setCreatedBy(user);
            sondage.setActive(true);
            sondage.setStartDate(new Date());
            if (sondage.getEndDate().before(new Date())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The deadline has already passed");
            }


            Sondage createdSondage = sondageService.addSondage(sondage);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSondage);
        } catch (MalformedJwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PutMapping("/update-sondage")
    public ResponseEntity<?> updateSondage(@RequestBody Sondage sondage) {
        try {
            // Ensure the sondage has a valid id for updating
            if (sondage.getIdSondage() == null) {
                return ResponseEntity.badRequest().body("Sondage ID is required for update.");
            }
            Sondage sondage1 = new Sondage();


            Sondage updatedSondage = sondageService.updateSondage(sondage);

            if (updatedSondage == null) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(updatedSondage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @DeleteMapping("/{id-sondage}/delete-sondage")
    public void removeSondage(@PathVariable("id-sondage") Integer idSondage){
        sondageService.removeSondage(idSondage);
    }
    @GetMapping("/sort/{sort-order}")
    public ResponseEntity<List<Sondage>> getSortedSondages(@PathVariable("sort-order") String sortOrder) {
        try {
            List<Sondage> sondages = sondageService.retrieveAllSondage();

            // Sort using Java Stream API
            List<Sondage> sortedSondages = sondages.stream()
                    .sorted((s1, s2) -> {
                        if ("asc".equalsIgnoreCase(sortOrder)) {
                            return s1.getEndDate().compareTo(s2.getEndDate());
                        } else if ("desc".equalsIgnoreCase(sortOrder)) {
                            return s2.getEndDate().compareTo(s1.getEndDate());
                        } else {
                            throw new IllegalArgumentException("Invalid sortOrder. Use 'asc' or 'desc'.");
                        }
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(sortedSondages);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/sondages/count_active")
    public ResponseEntity<Integer> countActiveSondages() {
        try {
            int activeSondageCount = sondageService.countActiveSondages();

            return ResponseEntity.ok(activeSondageCount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/summary")
    public List<Map<String, Object>> getSondageSummary() {
        List<Sondage> sondages = sondageRepo.findAllWithQuestionsAndReponses();

        return sondages.stream().map(this::mapSondageToSummary).collect(Collectors.toList());
    }

    private Map<String, Object> mapSondageToSummary(Sondage sondage) {
        int questionCount = sondage.getQuestions().size();
        int answerCount = sondage.getQuestions().stream()
                .flatMap(question -> question.getRepons().stream())
                .collect(Collectors.toList())
                .size();

        // Build summary map
        Map<String, Object> summary = Map.of(
                "title", sondage.getTitle(),
                "questionCount", questionCount,
                "answerCount", answerCount
        );

        return summary;
    }
    @GetMapping("/export-sondages")
    public void exportSondagesToExcel(@RequestHeader("Authorization") String token, HttpServletResponse response) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing token");
                return;
            }

            String jwtToken = token.substring(7);
            String userEmail = jwtUtils.extractUsername(jwtToken);

            Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "User not found or unauthorized");
                return;
            }

            // Set content type and headers for Excel file
            response.setContentType("application/vnd.ms-excel");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sondages.xls");

            // Call SondageService method to export data to Excel and write to response
            sondageService.exportToExcel(response);

            // Flush the response
            response.flushBuffer();
        } catch (MalformedJwtException | IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/participation-rates")
    public ResponseEntity<Map<Integer, Double>> getSondageParticipationRates() {
        Map<Integer, Double> participationRates = sondageService.calculateParticipationRatesForSondages();
        return ResponseEntity.ok(participationRates);
    }
    @PostMapping("/add-sondage-and-questions")
    public ResponseEntity<?> addSondageAndQuestions(@RequestBody SondageQuestionDTO sondageRequest,@RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Invalid or missing token
            }

            String jwtToken = token.substring(7);
            String userEmail = jwtUtils.extractUsername(jwtToken);

            Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // User not found or unauthorized
            }

            Users user = userOptional.get();
            sondageRequest.getSondage().setCreatedBy(user);
            sondageRequest.getSondage().setActive(true);
            sondageRequest.getSondage().setStartDate(new Date());

            if (sondageRequest.getSondage().getEndDate().before(new Date())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The deadline has already passed");
            }
            Sondage savedSondage = sondageService.addSondageAndQuestions(sondageRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(sondageRequest);
        } catch (MalformedJwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/add-participant-to-sondage/{sondageId}")
    public ResponseEntity<?> addUserToSondage(@RequestHeader("Authorization") String token,
                                              @PathVariable("sondageId") Integer sondageId) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Invalid or missing token
            }

            String jwtToken = token.substring(7);
            String userEmail = jwtUtils.extractUsername(jwtToken);

            // Retrieve user by email
            Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
            Users user = userOptional.get();

            // Retrieve sondage by ID
            Optional<Sondage> sondageOptional = sondageRepo.findById(sondageId);
            if (sondageOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sondage not found");
            }
            Sondage sondage = sondageOptional.get();

            // Add user to sondage participants
            sondage.addParticipant(user);
            sondageService.addSondage(sondage); // Save the updated sondage entity


            return ResponseEntity.status(HttpStatus.OK).body("User added to sondage successfully222");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding user to sondage: " + e.getMessage());
        }

    }
    @PatchMapping("/change-sondage-status/{sondageId}")
    public ResponseEntity<?> updateSondageStatus(@PathVariable Integer sondageId,
                                                 @RequestParam boolean isActive,
                                                 @RequestHeader("Authorization") String token) {
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Invalid or missing token
            }

            // Extract user email from token
            String jwtToken = token.substring(7);
            String userEmail = jwtUtils.extractUsername(jwtToken);

            // Check if user exists
            Optional<Users> userOptional = ourUserRepo.findByEmail(userEmail);


            // Update sondage status
            boolean updated = sondageService.updateSondageStatus(sondageId, isActive);
            if (updated) {
                String message = isActive ? "Sondage activated successfully" : "Sondage deactivated successfully";
                return ResponseEntity.ok().body("{\"message\": \"" + message + "\"}");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Sondage not found\"}");
            }
        } catch (MalformedJwtException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/get-participants-byIdSondage/{sondageId}")
    public ResponseEntity<List<Users>> getParticipantsBySondageId(@PathVariable Integer sondageId) {
        List<Users> participants = sondageService.getParticipantsBySondageId(sondageId);
        System.out.println(participants);
        return ResponseEntity.ok(participants);
    }
    @GetMapping("/export-single-sondage/{idSondage}")
    public ResponseEntity<String> exportSingleSondageToExcel(@PathVariable Integer idSondage, HttpServletResponse response) {
        try {
            Optional<Sondage> optionalSondage = sondageRepo.findByIdWithQuestionsAndReponses(idSondage);
            if (!optionalSondage.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            sondageService.exportToExcelSingleSondage(optionalSondage.get(), response);
            return ResponseEntity.ok().body("Sondage exported successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to export sondage: " + e.getMessage());
        }
    }
    @Scheduled(cron = "*/10 * * * * *")
    @GetMapping("/endingWithinNextWeek")
    public ResponseEntity<List<Sondage>> getSondagesEndingWithinNextWeek() {
        List<Sondage> sondages = sondageService.getSondagesEndingWithinNextWeek();
        if (sondages.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 No Content
        } else {
            return ResponseEntity.ok(sondages); // Return 200 OK with the list of sondages
        }
    }





}
