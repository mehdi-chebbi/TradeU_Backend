package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.Feedback;
import com.twd.SpringSecurityJWT.entity.Users;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface IFeedBackService {
    Feedback addFeedback(Feedback feedback);
    Feedback updateFeedback(Feedback feedback);

    void removeFeedback(Integer idFeedback);

    Optional<Feedback> retrieveFeedback(Integer idFeedback);

    List<Feedback> retrieveAllFeedback();
    List<Feedback> addListFeedback(List<Feedback> feedbacks);
    public Feedback getFeedbackById( Integer idFeedback);
    public List<Feedback> searchFeedbacks(Users createdBy, String contenu, Date submissionDate);

}
