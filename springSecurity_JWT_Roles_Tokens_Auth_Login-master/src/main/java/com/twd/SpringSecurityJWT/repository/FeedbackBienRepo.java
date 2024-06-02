package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.Feedback;
import com.twd.SpringSecurityJWT.entity.FeedbackBien;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackBienRepo extends JpaRepository<FeedbackBien, Integer> {
   List <FeedbackBien> findFeedbackBienByBienId(Integer bienId);
}
