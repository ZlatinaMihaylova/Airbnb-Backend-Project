package com.example.demo.controllers;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.example.demo.dto.SendMessageDTO;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.model.User;
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
import org.springframework.web.servlet.ModelAndView;


@RestController
public class MessageController {

	@Autowired
	private UserService userService;

	@Autowired
	private MessageService messageService;

	@GetMapping("/messages")
	public List<ChatListDTO> getAllMessages(HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
		long userId = UserService.authentication(request);
		return messageService.getAllMessagesForMessagePage(userService.getUserById(userId));
	}

	@GetMapping("/messages/userId={userId}")
	public List<ChatWithUserDTO> getMessagesWithUserById(@PathVariable long userId,HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException{
		long id = UserService.authentication(request);
		return messageService.getMessagesWithUser(userService.getUserById(id), userService.getUserById(userId));
	}

	@PostMapping("/messages/userId={receiverId}")
	public ModelAndView sendMessage(@PathVariable long receiverId, @RequestBody @Valid SendMessageDTO sendMessageDTO, HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
		long userId = UserService.authentication(request);
		messageService.sendMessage(userService.getUserById(userId), userService.getUserById(receiverId), sendMessageDTO);
		return new ModelAndView("redirect:/messages/" + receiverId);
	}
}
