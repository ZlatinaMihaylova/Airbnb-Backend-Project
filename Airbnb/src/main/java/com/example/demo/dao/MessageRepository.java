package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.City;
import com.example.demo.model.Message;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer>{
	
	Optional<Message> findById(long id);
}
