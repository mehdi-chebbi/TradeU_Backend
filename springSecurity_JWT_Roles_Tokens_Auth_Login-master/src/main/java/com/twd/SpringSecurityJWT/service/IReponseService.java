package com.twd.SpringSecurityJWT.service;


import com.twd.SpringSecurityJWT.entity.Reponse;

import java.util.List;

public interface IReponseService {
    Reponse addReponse(Reponse reponse);
    Reponse updateReponse(Reponse reponse);
    void removeReponseById(int idReponse);
    Reponse getReponse(int idReponse);
    List<Reponse> getAllReponses();
    String translateReponseToArabic(String text);
    String translateReponseToFrench(String text);
    String translateReponseToSpanish(String text);

    String parseGoogleTranslateResponse(String response);

    boolean ReponseContainsBadWord(Reponse reponse);

    long getNumberOfResponses();

}
