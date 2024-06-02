package com.twd.SpringSecurityJWT.email;

public interface EmailSender {
    void send(String to, String email);
}