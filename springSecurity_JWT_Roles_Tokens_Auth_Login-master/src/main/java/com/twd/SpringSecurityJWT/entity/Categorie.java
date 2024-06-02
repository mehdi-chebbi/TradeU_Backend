package com.twd.SpringSecurityJWT.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "categorie")
public class Categorie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;

    @OneToMany(mappedBy = "categorie")
    @JsonIgnore
    private List<Bien> biens;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @JsonIgnore
    private Users user;

    public Categorie(Integer id, String name, List<Bien> biens, Users user) {
        this.id = id;
        this.name = name;
        this.biens = biens;
        this.user = user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Categorie(Integer id, String name, String description, List<Bien> biens, Users user) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.biens = biens;
        this.user = user;
    }

    public Categorie() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Bien> getBiens() {
        return biens;
    }

    public void setBiens(List<Bien> biens) {
        this.biens = biens;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Categorie{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", user=" + (user != null ? user.getId() : null) +
                '}';
    }
}
