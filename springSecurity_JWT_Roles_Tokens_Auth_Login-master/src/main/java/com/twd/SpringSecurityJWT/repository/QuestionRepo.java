package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.Question;
import com.twd.SpringSecurityJWT.entity.Sondage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepo extends JpaRepository<Question, Integer> {
    public List<Question> findQuestionBySondage(Sondage sondage);
    public Question findQuestionById(Integer idQuestion);
}
