package com.example.demo.controllers;

import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.demo.exceptions.ElementNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ReviewsForRoomDTO;
import com.example.demo.dto.WriteReviewDTO;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.service.ReviewService;

@RestController
public class ReviewController {

	@Autowired
	private ReviewService reviewService;
	
	@GetMapping("/rooms/{roomId}/reviews")
	public Set<ReviewsForRoomDTO> getAllReviewsByRoomId(@PathVariable long roomId) throws ElementNotFoundException {
		return reviewService.getAllReviewsByRoomId(roomId).stream().map(review -> reviewService.convertReviewToDTO(review)).collect(Collectors.toSet());
	}
	
	@PostMapping("/rooms/{roomId}/reviews")
	public Set<ReviewsForRoomDTO> addReviewForRoom(@PathVariable long roomId, @RequestBody WriteReviewDTO reviewDTO,HttpServletRequest request) throws UnauthorizedException, ElementNotFoundException {
		long id = UserController.authentication(request);
		
		reviewService.addReviewForRoom(id, roomId, reviewDTO);
		return this.getAllReviewsByRoomId(roomId);

	}
}
