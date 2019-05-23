package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Review;
import com.example.demo.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>{
	
	Optional<Review> findById(long id);
	List<Review> findByRoomId(long roomId);
}
