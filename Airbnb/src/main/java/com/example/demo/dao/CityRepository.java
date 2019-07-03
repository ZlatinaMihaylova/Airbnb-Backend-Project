package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.City;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Integer>{

	Optional<City> findById(long id);
	Optional<City> findByName(String name);


}
