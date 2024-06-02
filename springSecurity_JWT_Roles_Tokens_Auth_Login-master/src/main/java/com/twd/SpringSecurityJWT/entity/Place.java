package com.twd.SpringSecurityJWT.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Data
@Entity
@Table(name = "place")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private String imageUrl;
    private boolean isReserved;

    @OneToOne(mappedBy = "place") // Correction: Changer OneToOne en OneToMany
    private Reservation reservation;

    // Constructeurs, getters et setters
}
