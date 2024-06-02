package com.twd.SpringSecurityJWT.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "Reponse")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reponse implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idReponse")
    private int idReponse;
    private String reponse;
    private LocalDate date_reponse;
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne
    private Publication publication;

    @ManyToOne
    private Users reponseCreatedBy;

    @ManyToMany
    @JoinTable(
            name = "reponse_likes",
            joinColumns = @JoinColumn(name = "reponse_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<Users> likedByUsers;

    @ManyToMany
    @JoinTable(
            name = "reponse_dislikes",
            joinColumns = @JoinColumn(name = "reponse_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<Users> dislikedByUsers;
    private int likes;
    private int dislikes;

}

