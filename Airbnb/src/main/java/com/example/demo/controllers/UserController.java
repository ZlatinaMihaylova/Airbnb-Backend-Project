package com.example.demo.controllers;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.example.demo.dto.*;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.SignUpException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.User;
import com.example.demo.service.RoomService;
import com.example.demo.service.UserService;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private RoomService roomService;

	@Autowired
	private BookingService bookingService;

	@PostMapping("/users")
	public void signUp(@RequestBody @Valid SignUpDTO signUpDTO, HttpServletRequest request) throws SignUpException, BadRequestException, NoSuchAlgorithmException, UnsupportedEncodingException{
		userService.signUp(signUpDTO,request.getSession());
	}

	@PostMapping("/login")
	public ModelAndView login(@RequestBody @Valid LoginDTO loginDTO, HttpServletRequest request) throws ElementNotFoundException, BadRequestException, NoSuchAlgorithmException, UnsupportedEncodingException {
		User user = userService.login(loginDTO, request.getSession());
		return new ModelAndView("redirect:/users/userId=" + user.getId());
	}

	@PostMapping("/logout")
	public void logout(HttpServletRequest request) throws BadRequestException {
		userService.logout(request.getSession());
	}

	@GetMapping("/users/userId={userId}")
	public GetUserProfileDTO getUserDetails(@PathVariable long userId) throws ElementNotFoundException {
		return userService.convertUserToDTO(userService.getUserById(userId));
	}

	@GetMapping("/profile")
	public ModelAndView getLoggedUserProfile(HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
		long userId = UserService.authentication(request);
		return new ModelAndView("redirect:/users/userId=" + userId);
	}

	@PutMapping("/changeInformation")
	public ModelAndView changeInformation(@RequestBody @Valid EditProfileDTO editProfileDTO, HttpServletRequest request) throws BadRequestException,UnauthorizedException, ElementNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {
		long userId = UserService.authentication(request);
		userService.changeInformation(userId, editProfileDTO);
		return new ModelAndView("redirect:/users/userId=" + userId);
	}


	@GetMapping("/viewFavourites")
	public List<GetListOfRoomDTO> viewFavouriteRooms(HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException{
		long id = UserService.authentication(request);
		return userService.viewFavouriteRooms(id).stream().map(room -> roomService.convertRoomToDTO(room)).collect(Collectors.toList());
	}

	@GetMapping("/myBookings")
	public Set<GetBookingInfoDTO> showMyBookings(HttpServletRequest request) throws UnauthorizedException{
		long id = UserService.authentication(request);
		return bookingService.getAllUsersBookings(id).stream().map(booking -> bookingService.convertBookingToDTO(booking)).collect(Collectors.toSet());
	}

}
