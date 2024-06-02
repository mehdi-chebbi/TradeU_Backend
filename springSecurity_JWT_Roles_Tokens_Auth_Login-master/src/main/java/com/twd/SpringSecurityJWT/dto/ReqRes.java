package com.twd.SpringSecurityJWT.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.twd.SpringSecurityJWT.entity.Reservation;
import com.twd.SpringSecurityJWT.entity.UserRole;
import com.twd.SpringSecurityJWT.entity.Users;
import com.twd.SpringSecurityJWT.entity.Place;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReqRes {

    private int statusCode;
    private String error;
    private Integer placeId;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private String email;
    private String phone;
    private String adress;
    private UserRole role;
    private String password;
    private List<Place> places;
    private Reservation reservation;
    private Users users;
    private String reservationDate;
    private boolean isReserved;
    private String verificationCode;
    private String imageUrl;
    private String description;

}