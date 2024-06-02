package com.twd.SpringSecurityJWT.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "Sondage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Sondage {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSondage;

    private String title;
    private String description;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "created_by_id", referencedColumnName = "id")
    private Users createdBy;

    // Establishing many-to-many relationship with participants (users participating in the survey)
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "sondage_participants",
            joinColumns = @JoinColumn(name = "sondage_id_sondage"),
            inverseJoinColumns = @JoinColumn(name = "participants_id")
    )
    private Set<Users> participants = new HashSet<>();

    // One-to-many relationship with questions
    @JsonIgnore
    @OneToMany(mappedBy = "sondage", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Question> questions = new HashSet<>();

    // One-to-many relationship with responses


    // Helper method to add a participant
    public void addParticipant(Users user) {
        participants.add(user);
    }

    // Helper method to remove a participant
    public void removeParticipant(Users user) {
        participants.remove(user);
    }

    // Helper method to add a question
    public void addQuestion(Question question) {
        question.setSondage(this);
        questions.add(question);
    }



    // Custom method to count the number of questions associated with this sondage
    @JsonIgnore
    public int getNumberOfQuestions() {
        return questions.size();
    }


}
