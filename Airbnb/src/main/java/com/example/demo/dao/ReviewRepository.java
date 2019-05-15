package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Review;
import com.example.demo.model.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>{
	
	Review findById(long id);
}
