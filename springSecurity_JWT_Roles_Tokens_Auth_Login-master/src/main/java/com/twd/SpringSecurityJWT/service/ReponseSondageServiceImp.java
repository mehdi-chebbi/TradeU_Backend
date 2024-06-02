package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.ReponseSondage;
import com.twd.SpringSecurityJWT.repository.ReponseRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@AllArgsConstructor
public class ReponseSondageServiceImp implements IReponseSondageService {
    ReponseRepo rs;
    @Override
    public ReponseSondage addReponse(ReponseSondage reponseSondage) {
        return rs.save(reponseSondage);
    }

    @Override
    public ReponseSondage updateReponse(ReponseSondage reponseSondage) {
        return rs.save(reponseSondage);
    }

    @Override
    public void removeReponse(Integer idReponse) {
        rs.deleteById(idReponse);

    }

    @Override
    public Optional<ReponseSondage> retrieveReponse(Integer idReponse) {
        return rs.findById(idReponse);
    }

    @Override
    public List<ReponseSondage> retrieveAllReponse() {
        return rs.findAll();
    }

    @Override
    public List<ReponseSondage> addListReponse(List<ReponseSondage> repons) {
        return rs.saveAll(repons);
    }
}
