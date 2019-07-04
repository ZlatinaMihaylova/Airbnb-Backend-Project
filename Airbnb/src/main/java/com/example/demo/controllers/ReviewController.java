package com.example.demo.controllers;

import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.service.RoomService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.GetReviewsForRoomDTO;
import com.example.demo.dto.AddReviewDTO;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.service.ReviewService;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ReviewController {

	@Autowired
	private UserService userService;

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private RoomService roomService;

	@GetMapping("/rooms/roomId={roomId}/reviews")
	public Set<GetReviewsForRoomDTO> getAllReviewsByRoomId(@PathVariable long roomId) throws ElementNotFoundException {
		return reviewService.getAllReviewsByRoom(roomService.getRoomById(roomId)).stream().map(review -> reviewService.convertReviewToDTO(review)).collect(Collectors.toSet());
	}

	@PostMapping("/rooms/roomId={roomId}/reviews")
	public ModelAndView addReviewForRoom(@PathVariable long roomId, @RequestBody @Valid AddReviewDTO reviewDTO, HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException, BadRequestException {
		long userId = UserService.authentication(request);
		reviewService.addReviewForRoom(userService.getUserById(userId), roomService.getRoomById(roomId), reviewDTO);
		return new ModelAndView("redirect:/rooms/roomId=" + roomId +"/reviews");
	}
}
