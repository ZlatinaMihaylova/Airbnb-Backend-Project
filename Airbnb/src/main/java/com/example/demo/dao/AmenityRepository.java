package com.example.demo.dao;

import com.example.demo.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Amenity;

import java.util.Optional;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Integer>{

    Optional<Amenity> findById(long id);
    Optional<Amenity> findByName(String name);

}
