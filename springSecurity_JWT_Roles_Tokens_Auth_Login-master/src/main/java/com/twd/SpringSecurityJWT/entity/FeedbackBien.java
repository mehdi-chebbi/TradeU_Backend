package com.twd.SpringSecurityJWT.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "FeedbackOnBien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackBien implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String contenu;
    private Date submissionDate2;
    @Enumerated(EnumType.STRING)
    private RateBien rating;
    @ManyToOne
    @JoinColumn(name = "bien_id", referencedColumnName = "id")
    private Bien bien;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;


}
