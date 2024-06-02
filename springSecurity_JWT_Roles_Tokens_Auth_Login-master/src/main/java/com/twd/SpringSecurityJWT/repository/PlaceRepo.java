package com.twd.SpringSecurityJWT.repository;

import com.twd.SpringSecurityJWT.entity.Place;
import com.twd.SpringSecurityJWT.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceRepo extends JpaRepository<Place, Integer> {
    Optional<Place> findById(Integer placeId);
    void deleteById(Integer placeId);
}
