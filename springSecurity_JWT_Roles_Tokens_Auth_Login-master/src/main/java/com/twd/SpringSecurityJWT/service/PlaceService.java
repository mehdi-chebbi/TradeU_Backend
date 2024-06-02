package com.twd.SpringSecurityJWT.service;

import com.twd.SpringSecurityJWT.entity.Place;
import com.twd.SpringSecurityJWT.entity.Reservation;
import com.twd.SpringSecurityJWT.repository.PlaceRepo;
import com.twd.SpringSecurityJWT.repository.ReservationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceService {
    @Autowired
    private PlaceRepo placeRepository;

    @Autowired
    private ReservationRepo reservationRepository;


}