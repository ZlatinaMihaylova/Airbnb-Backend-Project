package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.City;

@Repository
public interface CityRepository extends JpaRepository<City, Integer>{

	City findById(long id);
	City findByName(String name);

}
