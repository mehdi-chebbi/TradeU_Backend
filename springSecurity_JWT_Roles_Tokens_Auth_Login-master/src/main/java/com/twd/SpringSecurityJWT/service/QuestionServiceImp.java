package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.Question;
import com.twd.SpringSecurityJWT.entity.Sondage;
import com.twd.SpringSecurityJWT.repository.QuestionRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@AllArgsConstructor

public class QuestionServiceImp implements QuestionService {
    QuestionRepo qr;
    @Override
    public Question addQuestion(Question question) {
        return qr.save(question);
    }

    @Override
    public Question updateQuestion(Question question) {
        return qr.save(question);
    }

    @Override
    public void removeQuestion(Integer idQuestion) {
        qr.deleteById(idQuestion);

    }

    @Override
    public Optional<Question> retrieveQuestion(Integer idQuestion) {
        return qr.findById(idQuestion);
    }

    @Override
    public List<Question> retrieveAllQuestion() {
        return qr.findAll();
    }

    @Override
    public List<Question> addListQuestion(List<Question> questions) {
        return qr.saveAll(questions);
    }

    @Override
    public List<Question> findQuestionBySondage(Sondage sondage) {
        return qr.findQuestionBySondage(sondage);
    }
}
