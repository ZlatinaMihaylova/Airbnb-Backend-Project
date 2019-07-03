package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ReviewRepository;
import com.example.demo.dto.GetReviewsForRoomDTO;
import com.example.demo.dto.AddReviewDTO;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.Review;

@Service
public class ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

	@Autowired
	private RoomService roomService;

	@Autowired
	private UserService userService;

	public List<Review> getAllReviewsByRoomId(Long roomId) throws ElementNotFoundException {
		roomService.getRoomById(roomId);
		return reviewRepository.findByRoomId(roomId);
	}

	public void addReviewForRoom(long userId,long roomId, AddReviewDTO reviewDTO) throws ElementNotFoundException, UnauthorizedException,BadRequestException {
		LocalDateTime time = LocalDateTime.now();
		if ( roomService.getRoomById(roomId).getUserId() == userId) {
			throw new UnauthorizedException("User can not add review for his own room!");
		}
		if ( reviewDTO.getStars() < 1 || reviewDTO.getStars() > 5) {
			throw new BadRequestException("Wrong stars count!");
		}
		reviewRepository.saveAndFlush(new Review(null,time,reviewDTO.getText(),
				userService.getUserById(userId),
				roomService.getRoomById(roomId), reviewDTO.getStars()));
	}

	public double getRoomRating(Room room) {
		return reviewRepository.findByRoomId(room.getId()).stream()
				.mapToInt( review -> review.getStars()).average().orElse(0);
	}

	public int getRoomTimesRated(Room room)  {
		return (int) reviewRepository.findByRoomId(room.getId()).stream().count();
	}

	public void removeAllReviewsForRoom(long roomId) {
		List<Review> reviewsForRoom = reviewRepository.findByRoomId(roomId);
		reviewRepository.deleteAll(reviewsForRoom);
	}

	public List<Review> getAllReviewsForUser(long userId) throws ElementNotFoundException{
		userService.getUserById(userId);
		return reviewRepository.findAll().stream().filter(review -> review.getRoom().getUserId() == userId)
				.collect(Collectors.toList());
	}

	public GetReviewsForRoomDTO convertReviewToDTO(Review review) {
		return new GetReviewsForRoomDTO(review.getUser().viewAllNames(), review.getDate(), review.getStars(), review.getText());
	}
}
