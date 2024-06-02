package com.twd.SpringSecurityJWT.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
@Entity
@Getter
@Setter
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;

    @Getter
    @Setter
    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "place_id", referencedColumnName = "id")
    private Place place;

    //@Temporal(TemporalType.TIMESTAMP)
    private Date reservationDate;
    private LocalTime heureDebut;

    private LocalTime heureFin;
    private String description;


    // Constructeurs, getters et setters


    public Reservation() {
    }

    public Reservation(Integer id, Users user, Place place, Date reservationDate, String description, LocalTime heureDebut, LocalTime heureFin) {
        this.id = id;
        this.user = user;
        this.place = place;
        this.reservationDate = reservationDate;
        this.description = description;
        this.heureDebut = heureDebut;
        this.heureFin = heureFin;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", user=" + user +
                ", place=" + place +
                ", reservationDate=" + reservationDate +
                ", description=" + description +
                ", heureDebut=" + heureDebut +
                ", heureFin=" + heureFin +
                '}';
    }

    public Place getPlace() {
        return place;
    }
}
