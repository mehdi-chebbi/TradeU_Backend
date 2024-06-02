package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.Question;
import com.twd.SpringSecurityJWT.entity.Sondage;

import java.util.List;
import java.util.Optional;

public interface QuestionService {
    Question addQuestion(Question question);
    Question updateQuestion(Question question);

    void removeQuestion(Integer idQuestion);

    Optional<Question> retrieveQuestion(Integer idQuestion);

    List<Question> retrieveAllQuestion();
    List<Question> addListQuestion(List<Question> questions);
    public List<Question> findQuestionBySondage(Sondage sondage);


}
