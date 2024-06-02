package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.Feedback;
import com.twd.SpringSecurityJWT.entity.Users;
import com.twd.SpringSecurityJWT.repository.FeedbackRepo;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class FeedbackServiceImp implements IFeedBackService {
    FeedbackRepo feedbackRepo;
    @Autowired
    private EntityManager entityManager;
    @Override
    public Feedback addFeedback(Feedback feedback) {
        return feedbackRepo.save(feedback);
    }

    @Override
    public Feedback updateFeedback(Feedback updatedFeedback) {
        Integer feedbackId = updatedFeedback.getIdFeedback(); // Assuming there's an ID field

        // Retrieve existing Feedback entity from the database
        Optional<Feedback> existingFeedbackOptional = feedbackRepo.findById(feedbackId);

        if (existingFeedbackOptional.isEmpty()) {
            throw new RuntimeException("Feedback not found with ID: " + feedbackId);
        }

        Feedback existingFeedback = existingFeedbackOptional.get();

        // Update specific fields (excluding createdByFb)
        existingFeedback.setContenu(updatedFeedback.getContenu());
        existingFeedback.setSubmissionDate(updatedFeedback.getSubmissionDate());

        // Save the updated Feedback
        return feedbackRepo.save(existingFeedback);
    }

    @Override
    public void removeFeedback(Integer idFeedback) {
        feedbackRepo.deleteById(idFeedback);

    }

    @Override
    public Optional<Feedback> retrieveFeedback(Integer idFeedback) {
        return feedbackRepo.findById(idFeedback);
    }

    @Override
    public List<Feedback> retrieveAllFeedback() {
        return feedbackRepo.findAll();
    }

    @Override
    public List<Feedback> addListFeedback(List<Feedback> feedbacks) {
        return feedbackRepo.saveAll(feedbacks);
    }

    @Override
    public Feedback getFeedbackById(Integer idFeedback) {
        return null;
    }

    @Override
    public List<Feedback> searchFeedbacks(Users createdBy, String contenu, Date submissionDate) {
        List<Feedback> allFeedbacks = feedbackRepo.findAll();

        System.out.println("Received Criteria - createdBy: " + createdBy + ", contenu: " + contenu + ", submissionDate: " + submissionDate);
        System.out.println("Total Feedbacks: " + allFeedbacks.size());

        List<Feedback> filteredFeedbacks = allFeedbacks.stream()
                .filter(feedback ->
                        (createdBy == null || createdBy.getId() == feedback.getCreatedByFb().getId()) &&
                                (contenu == null || feedback.getContenu().contains(contenu)) &&
                                (submissionDate == null || feedback.getSubmissionDate().equals(submissionDate)))
                .collect(Collectors.toList());

        System.out.println("Filtered Feedbacks: " + filteredFeedbacks.size());

        // Log attributes of each filtered feedback
        filteredFeedbacks.forEach(feedback -> {
            System.out.println("Feedback ID: " + feedback.getIdFeedback());

        });

        return filteredFeedbacks;
    }




}
