package com.example.demo.dao;

import com.example.demo.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Amenity;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Integer>{

    Amenity findById(long id);
    Amenity findByName(String name);

}
