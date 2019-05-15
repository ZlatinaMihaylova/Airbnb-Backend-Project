package com.example.demo.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ChatListDTO;
import com.example.demo.dto.ChatWithUserDTO;
import com.example.demo.exceptions.NoMessagesException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.exceptions.UserException;
import com.example.demo.model.Message;
import com.example.demo.model.User;
import com.example.demo.service.MessageService;
//import com.example.demo.dao.MessageDao;
import com.example.demo.service.UserService;

@RestController
public class MessageController {
	
	
	@Autowired
	private MessageService messageService;
	
	
	@GetMapping("/messages")
	public Set<ChatListDTO> getAllMessages(HttpServletRequest request,HttpServletResponse response) throws UnauthorizedException{
		long id = UserController.authentication(request, response);  
		return messageService.getAllMessagesForMessagePage(id);
		
	}
	
	@GetMapping("/messages/{userId}")
	public Set<ChatWithUserDTO> getMessagesWithUserById(@PathVariable long userId,HttpServletRequest request,HttpServletResponse response) throws UnauthorizedException, NoMessagesException{
		
		long id = UserController.authentication(request, response);  
		if ( id == userId) {
			throw new UnauthorizedException("User can not have messages with himself!");
		}
		return messageService.getMessagesWithUserById(id, userId);
	}
	
	@PostMapping("/messages/{receiverId}")
	public Set<ChatWithUserDTO> sendMessage(@PathVariable long receiverId,@RequestBody String text,HttpServletRequest request,HttpServletResponse response) throws UnauthorizedException, NoMessagesException, UserException {
		
		long id = UserController.authentication(request, response);  
		if ( id == receiverId) {
			throw new UnauthorizedException("User can not send message to himself!");
		}
		messageService.sendMessage(id, receiverId, text);
		return this.getMessagesWithUserById(receiverId, request, response);
	}
}
