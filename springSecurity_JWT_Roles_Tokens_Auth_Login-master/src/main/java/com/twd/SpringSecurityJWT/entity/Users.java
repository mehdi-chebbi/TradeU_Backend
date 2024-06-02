package com.twd.SpringSecurityJWT.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@Entity
@Table(name = "Users")
    public class Users implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String email;
    private String name;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    private String phone;
    private String adress;
    private boolean banned;
    private boolean isOnline;

    private String verificationCode;

    @JsonIgnore
    @OneToOne(mappedBy = "user")
    private Cart cart;
    @JsonIgnore
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.REMOVE)
    private Set<Sondage> createdSondages = new HashSet<>();
    @JsonIgnore
    @ManyToMany(mappedBy = "participants", cascade = CascadeType.REMOVE)
    private Set<Sondage> participatedSondages = new HashSet<>();
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Set<ReponseSondage> createdReponsesSondage = new HashSet<>();
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Set<FeedbackBien> feedbackEntries = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.name()));
    }
    public int getNombreBienAvecBadWord() {
        return nombreBienAvecBadWord;
    }

    public void setNombreBienAvecBadWord(int nombreBienAvecBadWord) {
        this.nombreBienAvecBadWord = nombreBienAvecBadWord;
    }

    private int nombreBienAvecBadWord;

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

