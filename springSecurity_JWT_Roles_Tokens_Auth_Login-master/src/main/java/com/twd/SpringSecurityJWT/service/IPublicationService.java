package com.twd.SpringSecurityJWT.service;


import com.twd.SpringSecurityJWT.entity.Publication;

import java.util.List;

public interface IPublicationService {
    Publication addPublication(Publication publication);
    Publication updatePublication(Publication publication);
    void removePublicationById(int idPublication);
    Publication GetPublication(int idPublication);
    List<Publication> GetAllPublication();

    boolean publicationContainsBadWord(Publication publication);

    String translatePubToArabic(String text);

    String translatePubToFrench(String text);

    String translatePubToSpanish(String text);
    String parseGoogleTranslateResponse(String response);
    long getNumberOfPublications();

}