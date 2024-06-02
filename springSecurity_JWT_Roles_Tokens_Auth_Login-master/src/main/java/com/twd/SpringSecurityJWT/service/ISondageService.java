package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.dto.SondageQuestionDTO;
import com.twd.SpringSecurityJWT.entity.Sondage;
import com.twd.SpringSecurityJWT.entity.Users;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ISondageService {
    Sondage addSondage(Sondage sondage);
    Sondage updateSondage(Sondage sondage);

    void removeSondage(Integer idSondage);

    Sondage retrieveSondage(Integer idSondage);

    List<Sondage> retrieveAllSondage();
    Optional<Sondage> getSondagebyId(Integer idSondage);
    List<Sondage> addListSondage(List<Sondage> sondages);
    public int countActiveSondages();
    public List<Sondage> getSondagesEndingWithinNextWeek();
    public void showSondagesEndingWithinNextWeek();
    public void exportToExcel(HttpServletResponse response) throws IOException;
    public Map<Integer, Double> calculateParticipationRatesForSondages();
    public Sondage addSondageAndQuestions(SondageQuestionDTO sondageRequest);
    public void addUserToSondage(Users user, Sondage sondage);
    public boolean updateSondageStatus(Integer sondageId, boolean isActive);
    public List<Users> getParticipantsBySondageId(Integer sondageId);
    public void exportToExcelSingleSondage(Sondage sondage, HttpServletResponse response) throws IOException;


    }
