package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.FeedbackBien;

import java.util.List;
import java.util.Optional;

public interface IFeedBackBienService {
    FeedbackBien addFeedbackBien(FeedbackBien feedbackBien);
    FeedbackBien updateFeedbackBien(FeedbackBien feedback);

    void removeFeedbackBien(Integer idFeedbackbien);

    Optional<FeedbackBien> retrieveFeedbackBien(Integer idFeedbackbien);

    List<FeedbackBien> retrieveAllFeedbackBien();
    List<FeedbackBien> addListFeedbackBien(List<FeedbackBien> feedbackbiens);
    public Optional<FeedbackBien> getFeedbackBienId(Integer idFeedbackbien);

}
