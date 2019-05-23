package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Photo;
import com.example.demo.model.Room;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer>{
	
	Optional<Photo> findById(long id);

	List<Photo> findByRoomId(Long roomId);
}
