package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.Place;
import com.twd.SpringSecurityJWT.entity.Reservation;
import com.twd.SpringSecurityJWT.entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public interface ReservationRepo extends JpaRepository<Reservation, Integer> {


    @Query("SELECT r FROM Reservation r " +
            "WHERE (:userId IS NULL OR r.user.id = :userId) " +
            "AND (:placeId IS NULL OR r.place.id = :placeId) " +
            "AND (:reservationDate IS NULL OR r.reservationDate = :reservationDate) " +
            "AND (:description IS NULL OR r.description LIKE %:description%) " +
            "AND (:heureDebut IS NULL OR r.heureDebut = :heureDebut) " +
            "AND (:heureFin IS NULL OR r.heureFin = :heureFin)")
    List<Reservation> findByCriteria(@Param("userId") Integer userId,
                                     @Param("placeId") Integer placeId,
                                     @Param("reservationDate") Date reservationDate,
                                     @Param("description") String description,
                                     @Param("heureDebut") LocalTime heureDebut,
                                     @Param("heureFin") LocalTime heureFin);

    List<Reservation> findByPlace(Place place);

    Long countByPlaceId(Integer id);

    List<Reservation> findByUser(Users users);


    //@Modifying
    //@Transactional
    //@Query("UPDATE Place p SET p.isReserved = false " +
    //     "WHERE EXISTS (" +
    //     "   SELECT 1 FROM Reservation r " +
    //     "   WHERE r.place.id = p.id " +
    //     "   AND r.heureFin <= :currentHeure " +
    //     "   AND p.isReserved = true" +
    //    ")")
    //void updateIsReservedToFalseIfExpired(LocalTime currentHeure);

}






