package com.twd.SpringSecurityJWT.dto;


public class UserUpdateRequest {
    private String name;
    private String email;
    private String phone;
    private String adress;

    // Constructeur par défaut
    public UserUpdateRequest() {
    }

    // Constructeur avec tous les champs
    public UserUpdateRequest(String name, String email, String phone, String adress) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.adress = adress;
    }

    // Getters et setters pour chaque champ
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String address) {
        this.adress = address;
    }
}

