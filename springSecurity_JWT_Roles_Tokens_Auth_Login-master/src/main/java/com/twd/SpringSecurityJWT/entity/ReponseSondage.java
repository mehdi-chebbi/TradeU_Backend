package com.twd.SpringSecurityJWT.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@Entity
@Table(name = "ReponseSon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReponseSondage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idReponse;

    private String textReponse;

    @Transient
    private Integer questionId; // Transient field to hold the question_id

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "question_id") // Make sure to add insertable and updatable as false
    private Question question;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    public Integer getQuestionId() {
        return (this.question != null) ? this.question.getId() : null;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
        // Fetch the Question object only when needed
        if (this.question == null || !this.question.getId().equals(questionId)) {
            this.question = new Question();
            this.question.setId(questionId);
        }
    }
}
