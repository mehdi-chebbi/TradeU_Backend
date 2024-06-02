package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.Sondage;
import com.twd.SpringSecurityJWT.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SondageRepo extends JpaRepository<Sondage, Integer> {
    int countByIsActive(boolean isActive);
    List<Sondage> findByEndDateBetween(LocalDate startDate, LocalDate endDate);
    @Query("SELECT s FROM Sondage s LEFT JOIN FETCH s.questions q LEFT JOIN FETCH q.repons")
    List<Sondage> findAllWithQuestionsAndReponses();
    @Query("SELECT u FROM Users u INNER JOIN u.participatedSondages p WHERE p.idSondage = :sondageId")
    List<Users> findParticipantsBySondageId(@Param("sondageId") Integer sondageId);
    @Query("SELECT s FROM Sondage s LEFT JOIN FETCH s.questions q LEFT JOIN FETCH q.repons WHERE s.idSondage = ?1")
    Optional<Sondage> findByIdWithQuestionsAndReponses(Integer id);

}
