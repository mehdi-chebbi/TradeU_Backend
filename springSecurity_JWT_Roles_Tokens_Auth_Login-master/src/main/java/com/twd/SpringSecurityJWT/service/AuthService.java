package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.dto.ReqRes;
import com.twd.SpringSecurityJWT.entity.Users;
import com.twd.SpringSecurityJWT.repository.OurUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import java.util.Optional;

import java.util.HashMap;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private OurUserRepo ourUserRepo;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    private static final String ACCOUNT_SID = "AC3dcac380e1fbb675f3d2e176455f1729";
    private static final String AUTH_TOKEN = "7cc2ab2de8e523c5a1f222e4a6091906";
    private static final String TWILIO_PHONE_NUMBER = "+16562203492";

    public ReqRes signUp(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();
        try {

            if (ourUserRepo.findByEmail(registrationRequest.getEmail()).isPresent()) {
                // Si un utilisateur avec cet email existe déjà, afficher un message d'erreur
                resp.setStatusCode(400); // Bad Request
                resp.setMessage("Email already exists");
                return resp;
            }

            Users users = new Users();
            users.setEmail(registrationRequest.getEmail());
            users.setName(registrationRequest.getName());
            users.setPhone(registrationRequest.getPhone());
            users.setAdress(registrationRequest.getAdress());
            users.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            users.setRole(registrationRequest.getRole());


            Users ourUserResult = ourUserRepo.save(users);
            if (ourUserResult != null && ourUserResult.getId() > 0) {
                resp.setUsers(ourUserResult);
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public ReqRes signIn(ReqRes signinRequest) {
        ReqRes response = new ReqRes();

        try {
            // Vérifier si l'utilisateur est banni
            var userOptional = ourUserRepo.findByEmail(signinRequest.getEmail());
            if (userOptional.isPresent()) {
                var user = userOptional.get();

            } else {
                response.setStatusCode(404); // 404 Not Found
                response.setMessage("User not found.");
                return response;
            }

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword()));
            var user = ourUserRepo.findByEmail(signinRequest.getEmail()).orElseThrow();
            if (user.isBanned()) {
                response.setStatusCode(403); // 403 Forbidden
                response.setMessage("User is banned. Access denied.");
                return response;
            }
            System.out.println("USER IS: " + user);
            var jwt = jwtUtils.generateToken(user);
            // user.setToken(jwt);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hr");
            response.setRole(user.getRole());
            response.setName(user.getName());
            response.setAdress(user.getAdress());
            response.setPhone(user.getPhone());
            response.setMessage("Successfully Signed In");
            user.setOnline(true);
            ourUserRepo.save(user);


        } catch (Exception e) {
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }


    public ReqRes refreshToken(ReqRes refreshTokenReqiest) {
        ReqRes response = new ReqRes();
        String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
        Users users = ourUserRepo.findByEmail(ourEmail).orElseThrow();
        if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
            var jwt = jwtUtils.generateToken(users);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshTokenReqiest.getToken());
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Refreshed Token");
        }
        response.setStatusCode(500);
        return response;
    }


    private void sendMessage(String message) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message.creator(
                        new com.twilio.type.PhoneNumber("+21652467059"),
                        new com.twilio.type.PhoneNumber("++17372010805"),
                        message)
                .create();

    }}




