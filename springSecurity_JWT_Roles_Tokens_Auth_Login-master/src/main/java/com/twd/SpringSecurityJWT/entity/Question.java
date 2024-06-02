package com.twd.SpringSecurityJWT.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String text;
    @ManyToOne
    @JoinColumn(name = "sondage_id")
    private Sondage sondage;
    @OneToMany(mappedBy = "question",cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReponseSondage> repons = new HashSet<>();



}
