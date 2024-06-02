package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.ReponseSondage;

import java.util.List;
import java.util.Optional;

public interface IReponseSondageService {
    ReponseSondage addReponse(ReponseSondage reponseSondage);
    ReponseSondage updateReponse(ReponseSondage reponseSondage);

    void removeReponse(Integer idReponse);

    Optional<ReponseSondage> retrieveReponse(Integer idReponse);

    List<ReponseSondage> retrieveAllReponse();
    List<ReponseSondage> addListReponse(List<ReponseSondage> repons);

}
