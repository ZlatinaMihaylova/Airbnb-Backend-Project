package com.example.demo.dao;

import com.example.demo.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Room;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer>{

	Optional<Room> findById(long id);
	List<Room> findByUserId(long userId);
	List<Room> findByCityName(String cityName);
}
