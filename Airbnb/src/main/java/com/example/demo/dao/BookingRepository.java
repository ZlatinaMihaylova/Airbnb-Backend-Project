package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Booking;
import com.example.demo.model.Room;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer>{

	Optional<Booking> findById(long id);
	List<Booking> findByRoomId(long id);
	List<Booking> findByUserId(long id);
}
