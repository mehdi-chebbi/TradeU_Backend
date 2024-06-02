package com.twd.SpringSecurityJWT.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Getter
@Setter
@Table(name = "biens")

public class    Bien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nom;
    private String discription;
    private Float prix;
    private LocalDate dateAjout;
    private boolean autorise = false;
    private String imageUrl;
    private Integer badfeedcount=0;
    @JsonIgnore
    @ManyToMany(mappedBy = "biens", cascade = CascadeType.REMOVE)
    private Set<Cart> carts = new HashSet<>();


    @JsonIgnore
    @OneToMany(mappedBy = "bien", cascade = CascadeType.ALL)
    private Set<FeedbackBien> feedbackEntries= new HashSet<>();



    // Getters et setters pour autorise
    public boolean isAutorise() {
        return autorise;
    }

    public void setAutorise(boolean autorise) {
        this.autorise = autorise;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;
/////////////


    @Transient
    @JsonIgnore
    private MultipartFile file;
    public Bien(Integer id, String nom, String discription, Float prix, LocalDate dateAjout, Categorie categorie, Users user,String imageUrl) {
        this.id = id;
        this.nom = nom;
        this.discription = discription;
        this.prix = prix;
        this.dateAjout = getDateAjout();
        this.categorie = categorie;
        this.user = user;
        this.autorise = false;
        this.imageUrl = imageUrl;

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Bien() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public Float getPrix() {
        return prix;
    }

    public void setPrix(Float prix) {
        this.prix = prix;
    }

    public LocalDate getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(LocalDate dateAjout) {
        this.dateAjout = dateAjout;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Bien{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", discription='" + discription + '\'' +
                ", prix=" + prix +
                ", dateAjout=" + dateAjout +
                ", categorieId=" + (categorie != null ? categorie.getId() : null) + // Limiter à l'ID de la catégorie
                ", userId=" + (user != null ? user.getId() : null) + // Limiter à l'ID de l'utilisateur
                '}';
    }

}
