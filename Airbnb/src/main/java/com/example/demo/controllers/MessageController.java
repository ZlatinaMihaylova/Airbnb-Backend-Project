package com.example.demo.controllers;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ChatListDTO;
import com.example.demo.dto.ChatWithUserDTO;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.service.MessageService;


@RestController
public class MessageController {

	@Autowired
	private MessageService messageService;
	
	
	@GetMapping("/messages")
	public List<ChatListDTO> getAllMessages(HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
		long id = UserService.authentication(request);
		return messageService.getAllMessagesForMessagePage(id);
	}
	
	@GetMapping("/messages/{userId}")
	public List<ChatWithUserDTO> getMessagesWithUserById(@PathVariable long userId,HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException{
		long id = UserService.authentication(request);
		if ( id == userId) {
			throw new UnauthorizedException("User can not have messages with himself!");
		}
		return messageService.getMessagesWithUserById(id, userId);
	}
	
	@PostMapping("/messages/{receiverId}")
	public List<ChatWithUserDTO> sendMessage(@PathVariable long receiverId,@RequestBody String text,HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
		long id = UserService.authentication(request);
		if ( id == receiverId) {
			throw new UnauthorizedException("User can not send message to himself!");
		}
		messageService.sendMessage(id, receiverId, text);
		return this.getMessagesWithUserById(receiverId, request);
	}
}
