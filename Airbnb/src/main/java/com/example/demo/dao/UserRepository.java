package com.example.demo.dao;

import org.hibernate.sql.Update;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Message;
import com.example.demo.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	
	User findById(long id);
}
