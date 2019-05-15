package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer>{

	Room findById(long id);

}
