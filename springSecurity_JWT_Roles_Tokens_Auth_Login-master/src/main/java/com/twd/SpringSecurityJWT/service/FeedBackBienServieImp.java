package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.FeedbackBien;
import com.twd.SpringSecurityJWT.repository.FeedbackBienRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class FeedBackBienServieImp implements IFeedBackBienService {
    @Autowired
    FeedbackBienRepo feedbackBienRepo;
    @Override
    public FeedbackBien addFeedbackBien(FeedbackBien feedbackBien) {
        return feedbackBienRepo.save(feedbackBien) ;
    }

    @Override
    public FeedbackBien updateFeedbackBien(FeedbackBien feedbackBien) {
        return feedbackBienRepo.save(feedbackBien);
    }

    @Override
    public void removeFeedbackBien(Integer idFeedbackbien) {
        feedbackBienRepo.deleteById(idFeedbackbien);

    }

    @Override
    public Optional<FeedbackBien> retrieveFeedbackBien(Integer idFeedbackbien) {
        return feedbackBienRepo.findById(idFeedbackbien);
    }

    @Override
    public List<FeedbackBien> retrieveAllFeedbackBien() {
        return feedbackBienRepo.findAll();
    }

    @Override
    public List<FeedbackBien> addListFeedbackBien(List<FeedbackBien> feedbackbiens) {
        return feedbackBienRepo.saveAll(feedbackbiens);
    }

    @Override
    public Optional<FeedbackBien> getFeedbackBienId(Integer idFeedbackbien) {
        return feedbackBienRepo.findById(idFeedbackbien);
    }
}
