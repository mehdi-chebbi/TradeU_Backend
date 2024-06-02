package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.Feedback;
import com.twd.SpringSecurityJWT.entity.FeedbackSearchCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FeedbackRepo extends JpaRepository<Feedback, Integer> {
    @Query("SELECT f FROM Feedback f WHERE " +
            "(:#{#criteria.createdBy} IS NULL OR f.createdByFb = :#{#criteria.createdBy}) " +
            "OR (:#{#criteria.contenu} IS NULL OR f.contenu LIKE %:#{#criteria.contenu}%) " +
            "OR (:#{#criteria.submissionDate} IS NULL OR f.submissionDate = :#{#criteria.submissionDate})")
    List<Feedback> findFeedbacksByCriteria(@Param("criteria") FeedbackSearchCriteria criteria);
}
