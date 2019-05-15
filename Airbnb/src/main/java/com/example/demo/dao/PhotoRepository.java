package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Photo;
import com.example.demo.model.Room;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer>{
	
	Photo findById(long id);
}
