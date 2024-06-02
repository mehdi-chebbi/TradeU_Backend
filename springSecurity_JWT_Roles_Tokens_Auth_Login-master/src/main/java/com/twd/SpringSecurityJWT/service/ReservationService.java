package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.Place;
import com.twd.SpringSecurityJWT.entity.Reservation;
import com.twd.SpringSecurityJWT.repository.PlaceRepo;
import com.twd.SpringSecurityJWT.repository.ReservationRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service; // Ajout de l'import pour @Service

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReservationService {
    private final ReservationRepo reservationRepo;
    private final PlaceRepo placeRepo;

    public ReservationService(ReservationRepo reservationRepo, PlaceRepo placeRepo) {
        this.reservationRepo = reservationRepo;
        this.placeRepo = placeRepo; // Injection de PlaceRepo
    }



    @Scheduled(fixedRate = 60000)
    public void checkAndUpdateReservationStatus() {
        List<Reservation> reservations = reservationRepo.findAll();
        LocalDateTime currentDateTime = LocalDateTime.now();

        for (Reservation reservation : reservations) {
            LocalTime reservationEndTime = reservation.getHeureFin();
            LocalDateTime reservationEndDateTime = LocalDateTime.of(
                    reservation.getReservationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                    reservationEndTime
            );

            if (reservationEndDateTime.isBefore(currentDateTime)) {
                Place place = reservation.getPlace();
                place.setReserved(false);
                placeRepo.save(place);
            }
        }
    }

    public Map<Place, Long> getReservationCountByPlace() {
        List<Place> places = placeRepo.findAll();
        Map<Place, Long> reservationCountByPlace = new HashMap<>();

        for (Place place : places) {
            Long reservationCount = reservationRepo.countByPlaceId(place.getId());
            reservationCountByPlace.put(place, reservationCount);
        }

        return reservationCountByPlace;
    }
}
