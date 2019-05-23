package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Message;
import com.example.demo.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	
	Optional<User> findById(long id);
	Optional<User> findByEmailAndPassword(String email, String password);
}
