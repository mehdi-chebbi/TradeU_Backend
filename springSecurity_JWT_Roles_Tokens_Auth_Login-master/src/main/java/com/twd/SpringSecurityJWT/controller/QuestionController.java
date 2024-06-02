package com.twd.SpringSecurityJWT.controller;

import com.twd.SpringSecurityJWT.entity.Question;
import com.twd.SpringSecurityJWT.entity.Sondage;
import com.twd.SpringSecurityJWT.service.QuestionService;
import com.twd.SpringSecurityJWT.service.SondageServiceImp;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("QuestionController")
public class QuestionController {
    SondageServiceImp sondageService;
    QuestionService questionService;
    @GetMapping("/retrieve-All-Question")
    public List<Question> GetAllQuestion(){
        return questionService.retrieveAllQuestion();
    }
    @PostMapping("/add-question")
    public Question addQuestion(@RequestBody Question question) {return questionService.addQuestion(question);}
    @PutMapping("/update-question")
    public Question updateQuestion(@RequestBody Question question){return questionService.updateQuestion(question);}
    @DeleteMapping("/{id-question}/delete-Question")
    public void removeQuestion(@PathVariable("id-question") Integer idQuestion){
        questionService.removeQuestion(idQuestion);
    }
    @PostMapping("/add-list-Question")
    public List<Question> addListQuestion(@RequestBody List<Question> questions) {return questionService.addListQuestion(questions);}

    @GetMapping("/get-question-byIdSondage/{id-sondage}")
    public List<Question> findQuestionBySondage(@PathVariable("id-sondage") Integer idSondage) {
        Optional<Sondage> optionalSondage = sondageService.getSondagebyId(idSondage);
        Sondage sondage = optionalSondage.orElseThrow();
        return questionService.findQuestionBySondage(sondage);
    }


}
