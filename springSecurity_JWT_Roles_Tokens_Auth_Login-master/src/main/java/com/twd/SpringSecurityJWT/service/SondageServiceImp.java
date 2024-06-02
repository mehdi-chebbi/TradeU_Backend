package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.dto.SondageQuestionDTO;
import com.twd.SpringSecurityJWT.entity.Question;
import com.twd.SpringSecurityJWT.entity.Sondage;
import com.twd.SpringSecurityJWT.entity.Users;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import com.twd.SpringSecurityJWT.repository.QuestionRepo;
import com.twd.SpringSecurityJWT.repository.SondageRepo;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class SondageServiceImp implements ISondageService{
SondageRepo sondageRepo;
     QuestionRepo questionRepository;
     OurUserRepo userRepo;

    @Override
    public Sondage addSondage(Sondage sondage) {
        return sondageRepo.save(sondage);
    }

    @Override
    public Sondage updateSondage(Sondage updatedSondage) {
        Integer sondageId = updatedSondage.getIdSondage(); // Assuming there's an ID field

        // Retrieve existing Sondage entity from database
        Optional<Sondage> existingSondageOptional = sondageRepo.findById(sondageId);

        if (existingSondageOptional.isEmpty()) {
            throw new RuntimeException("Sondage not found with ID: " + sondageId);
        }

        Sondage existingSondage = existingSondageOptional.get();

        // Update specific fields (excluding createdBy)
        existingSondage.setTitle(updatedSondage.getTitle());
        existingSondage.setDescription(updatedSondage.getDescription());
        existingSondage.setStartDate(updatedSondage.getStartDate());
        existingSondage.setEndDate(updatedSondage.getEndDate());
        existingSondage.setActive(updatedSondage.isActive());

        // Save the updated Sondage
        return sondageRepo.save(existingSondage);
    }
    @Override
    public void removeSondage(Integer idSondage) {
        sondageRepo.deleteById(idSondage);

    }

    @Override
    public Sondage retrieveSondage(Integer idSondage) {
        return sondageRepo.findById(idSondage).orElse(null);
    }

    @Override
    public List<Sondage> retrieveAllSondage() {
        return sondageRepo.findAll();
    }

    @Override
    public Optional<Sondage> getSondagebyId(Integer idSondage) {
        return sondageRepo.findById(idSondage);
    }

    @Override
    public List<Sondage> addListSondage(List<Sondage> sondages) {
        return sondageRepo.saveAll(sondages);
    }
    @Override
    public int countActiveSondages() {
        return sondageRepo.countByIsActive(true);
    }
    @Override
    public List<Sondage> getSondagesEndingWithinNextWeek() {
        LocalDate now = LocalDate.now();
        LocalDate oneWeekLater = now.plusDays(7);
        List<Sondage> s = sondageRepo.findByEndDateBetween(now, oneWeekLater);

        if (s.isEmpty()) {
            log.info("No surveys found ending within the next week.");

            return Collections.emptyList();
        } else {
            return s;
        }
    }
    @Override
    @Transactional
    public void showSondagesEndingWithinNextWeek() {
        List<Sondage> sondages = getSondagesEndingWithinNextWeek();
        for (Sondage sondage : sondages) {
            System.out.println("Sondage ending within next week: " + sondage.getTitle());


        }
    }
    @Override
    public void exportToExcel(HttpServletResponse response) throws IOException {
        Iterable<Sondage> sondages = sondageRepo.findAllWithQuestionsAndReponses();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sondages");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Title");
        headerRow.createCell(1).setCellValue("Description");
        headerRow.createCell(2).setCellValue("Start Date");
        headerRow.createCell(3).setCellValue("End Date");
        headerRow.createCell(4).setCellValue("Number of Questions");
        headerRow.createCell(5).setCellValue("Number of Answers");
        headerRow.createCell(6).setCellValue("Participation Rate");


        // Populate data rows
        int rowNum = 1;
        for (Sondage sondage : sondages) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(sondage.getTitle());
            row.createCell(1).setCellValue(sondage.getDescription());
            row.createCell(2).setCellValue(sondage.getStartDate().toString());
            row.createCell(3).setCellValue(sondage.getEndDate().toString());
            row.createCell(4).setCellValue(sondage.getQuestions().size()); // Number of questions

            // Calculate total number of answers (responses) for this sondage
            int totalAnswers = 0;
            for (Question question : sondage.getQuestions()) {
                totalAnswers += question.getRepons().size();
            }
            row.createCell(5).setCellValue(totalAnswers); // Number of answers
            row.createCell(6).setCellValue(this.calculateParticipationRateUsers(sondage)+"%"); // Number of answers

        }

        // Set content type and headers for Excel file download
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=sondages.xlsx");

        // Write workbook data to response output stream
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);

        // Close workbook and output stream
        workbook.close();
        outputStream.close();
    }

    @Override
    public Map<Integer, Double> calculateParticipationRatesForSondages() {
        List<Sondage> sondages = sondageRepo.findAllWithQuestionsAndReponses();

        if (sondages == null || sondages.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Integer, Double> participationRates = new HashMap<>();

        for (Sondage sondage : sondages) {
            double participationRate = calculateParticipationRate(sondage);
            participationRates.put(sondage.getIdSondage(), participationRate);
        }

        return participationRates;
    }

    private double calculateParticipationRate(Sondage sondage) {
        if (sondage == null || sondage.getQuestions() == null || sondage.getQuestions().isEmpty()) {
            return 0.0;
        }

        int totalQuestions = sondage.getQuestions().size();
        int totalAnsweredQuestions = 0;

        for (Question question : sondage.getQuestions()) {
            if (!question.getRepons().isEmpty()) {
                totalAnsweredQuestions++;
            }
        }

        if (totalQuestions == 0) {
            return 0.0;
        }

        return (double) totalAnsweredQuestions / totalQuestions * 100;
    }
    @Transactional
    public Sondage addSondageAndQuestions(SondageQuestionDTO sondageRequest) {
        Sondage sondage = sondageRequest.getSondage();
        List<String> questions = sondageRequest.getQuestions();

        // Save sondage to get its ID
        sondage = sondageRepo.save(sondage);

        // Save questions associated with the sondage
        for (String questionText : questions) {
            Question question = new Question();
            question.setSondage(sondage);
            question.setText(questionText);
            // Set other question properties
            questionRepository.save(question);
        }

        return sondage;
    }
    @Override
    public void addUserToSondage(Users user, Sondage sondage) {
        // Check if the user is already a participant in the sondage
        if (!sondage.getParticipants().contains(user)) {
            // Add the user to the sondage participants
            sondage.addParticipant(user);
            // Save the sondage to update the association
            sondageRepo.save(sondage);
        }
    }
    @Override
    public boolean updateSondageStatus(Integer sondageId, boolean isActive) {
        Optional<Sondage> sondageOptional = sondageRepo.findById(sondageId);
        if (sondageOptional.isPresent()) {
            Sondage sondage = sondageOptional.get();
            sondage.setActive(isActive);
            sondageRepo.save(sondage);
            return true;
        }
        return false;
    }
    @Override
    public List<Users> getParticipantsBySondageId(Integer sondageId) {
        return sondageRepo.findParticipantsBySondageId(sondageId);
    }
    @Override
    public void exportToExcelSingleSondage(Sondage sondage, HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sondage");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Title");
        headerRow.createCell(1).setCellValue("Description");
        headerRow.createCell(2).setCellValue("Start Date");
        headerRow.createCell(3).setCellValue("End Date");
        headerRow.createCell(4).setCellValue("Number of Questions");
        headerRow.createCell(5).setCellValue("Number of Answers");
        headerRow.createCell(6).setCellValue("Participation Rate");



        // Populate data row
        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(sondage.getTitle());
        row.createCell(1).setCellValue(sondage.getDescription());
        row.createCell(2).setCellValue(sondage.getStartDate().toString());
        row.createCell(3).setCellValue(sondage.getEndDate().toString());
        row.createCell(4).setCellValue(sondage.getQuestions().size()); // Number of questions



        // Calculate total number of answers (responses) for this sondage
        int totalAnswers = 0;
        for (Question question : sondage.getQuestions()) {
            totalAnswers += question.getRepons().size();
        }
        row.createCell(5).setCellValue(totalAnswers); // Number of answers
        row.createCell(6).setCellValue(this.calculateParticipationRateUsers(sondage)); // Number of answers


        // Set content type and headers for Excel file download
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=sondage.xlsx");

        // Write workbook data to response output stream
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);

        // Close workbook and output stream
        workbook.close();
        outputStream.close();
    }
    private double calculateParticipationRateUsers(Sondage sondage) {
        int totalUsers= userRepo.findAll().size();

        if (sondage == null || sondage.getQuestions() == null || sondage.getQuestions().isEmpty()) {
            return 0.0;
        }

        int totalQuestions = sondage.getQuestions().size();
        int totalAnsweredQuestions = 0;

        // Count the total number of answered questions
        for (Question question : sondage.getQuestions()) {
            if (!question.getRepons().isEmpty()) {
                totalAnsweredQuestions++;
            }
        }

        // Calculate participation rate
        if (totalQuestions == 0) {
            return 0.0;
        }

        // Calculate the participation rate based on the number of answered questions and total users
        double participationRate = ((double) totalAnsweredQuestions / totalQuestions) * 100;

        // Adjust the participation rate by considering total users
        return (double) totalAnsweredQuestions / totalUsers * 100;
    }






}
