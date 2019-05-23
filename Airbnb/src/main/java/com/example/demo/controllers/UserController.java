package com.example.demo.controllers;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.example.demo.exceptions.ElementNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.EditProfileDTO;
import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.RoomListDTO;
import com.example.demo.dto.UserBookingsDTO;
import com.example.demo.dto.UserProfileDTO;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.SignUpException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.User;
import com.example.demo.service.RoomService;
import com.example.demo.service.UserService;

@RestController
public class UserController {
	
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoomService roomService;
	
	static long authentication(HttpServletRequest request) throws UnauthorizedException {
		HttpSession session = request.getSession();
		if (session.getAttribute("userId") == null) {
			throw new UnauthorizedException("You must login first");
		}
		
		long id = (long) session.getAttribute("userId"); 
		return id;
	}
	
	@PostMapping("/users")
	public long signUp(@RequestBody User user,HttpServletRequest request) throws SignUpException, BadRequestException, NoSuchAlgorithmException, UnsupportedEncodingException{
		HttpSession session = request.getSession();
		if (session.getAttribute("userId") != null) {
			throw new BadRequestException("User is already logged in");
		}
		return userService.signUp(user);
		
	}
	
	@GetMapping("/users")
	public Set<User> getAllUsers(){
		return userService.getAllUsers();
	}
	
	@GetMapping("/users/{userId}")
	public UserProfileDTO getUserDetails(@PathVariable long userId) throws ElementNotFoundException {
		return userService.getUserById(userId);
	}
	
	@PostMapping("/login")
	public void login(@RequestBody LoginDTO user, HttpServletRequest request) throws ElementNotFoundException, BadRequestException, NoSuchAlgorithmException, UnsupportedEncodingException {
		HttpSession session = request.getSession();
		if (session.getAttribute("userId") != null) {
			throw new BadRequestException("User is already logged in!");
		}
		User u = userService.login(user);
		session = request.getSession();
		session.setAttribute("userId", u.getId());
	}
	
	@PostMapping("/logout")
	public void logout(HttpServletRequest request) throws BadRequestException {
		HttpSession session = request.getSession();
		if (session.getAttribute("userId") == null) {
			throw new BadRequestException("You must login first");
		}
		session.invalidate();
	}
	
	@PutMapping("/changeInformation")
	public UserProfileDTO changeInformation(@RequestBody EditProfileDTO editProfileDTO,HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {
		
		long id = UserController.authentication(request);
		return userService.changeInformation(id, editProfileDTO);
	}
	@GetMapping("/profile")
	public UserProfileDTO getUserProfile(HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
		
		long id = UserController.authentication(request);
		return userService.getUserById(id);
	}
	
	@GetMapping("/viewFavourites")
	public List<RoomListDTO> viewFavourites(HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException{
		
		long id = UserController.authentication(request);
		return userService.viewFavouritesRoom(id);
	}
	
	@GetMapping("/myBookings")
	public Set<UserBookingsDTO> showMyBookings(HttpServletRequest request) throws UnauthorizedException{
		
		long id = UserController.authentication(request);
		return userService.showMyBookings(id);
	}
}
