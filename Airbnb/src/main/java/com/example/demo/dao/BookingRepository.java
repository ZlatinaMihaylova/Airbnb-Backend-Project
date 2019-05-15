package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Booking;
import com.example.demo.model.Room;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer>{
	Booking findById(long id);
}
