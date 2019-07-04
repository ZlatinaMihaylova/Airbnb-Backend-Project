package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.model.Room;
import com.example.demo.model.User;
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

	public List<Review> getAllReviewsByRoom(Room room) {
		return reviewRepository.findByRoomId(room.getId());
	}

	public void addReviewForRoom(User user, Room room, AddReviewDTO reviewDTO) throws  UnauthorizedException,BadRequestException {
		LocalDateTime time = LocalDateTime.now();
		if ( room.getUserId() == user.getId()) {
			throw new UnauthorizedException("User can not add review for his own room!");
		}
		if ( reviewDTO.getStars() < 1 || reviewDTO.getStars() > 5) {
			throw new BadRequestException("Wrong stars count!");
		}
		reviewRepository.saveAndFlush(new Review(null,time,reviewDTO.getText(),
				user, room, reviewDTO.getStars()));
	}

	public double getRoomRating(Room room) {
		return reviewRepository.findByRoomId(room.getId()).stream()
				.mapToInt( review -> review.getStars()).average().orElse(0);
	}

	public int getRoomTimesRated(Room room)  {
		return (int) reviewRepository.findByRoomId(room.getId()).stream().count();
	}

	public void removeAllReviewsForRoom(Room room) {
		List<Review> reviewsForRoom = reviewRepository.findByRoomId(room.getId());
		reviewRepository.deleteAll(reviewsForRoom);
	}

	public List<Review> getAllReviewsForUser(User user) {
		return reviewRepository.findAll().stream().filter(review -> review.getRoom().getUserId() == user.getId())
				.collect(Collectors.toList());
	}

	public GetReviewsForRoomDTO convertReviewToDTO(Review review) {
		return new GetReviewsForRoomDTO(review.getUser().viewAllNames(), review.getDate(), review.getStars(), review.getText());
	}

	public double getRoomBayesianWeightedRating(Room room){
		double averageRating = getRoomRating(room);
		double averageRatingForAllRooms = roomService.getAllRooms().stream()
				.mapToDouble(roomOfAll -> getRoomRating(roomOfAll)).average().orElse(0);
		int numberOfVotes = reviewRepository.findByRoomId(room.getId()).size();
		double averageNumberOfVotesForAllRooms = roomService
				.getAllRooms()
				.stream()
				.mapToInt( roomOfAll -> reviewRepository.findByRoomId(roomOfAll.getId()).size())
				.average().orElse(0);
		double bayesianWeight = numberOfVotes / (numberOfVotes + averageNumberOfVotesForAllRooms);
		return bayesianWeight * averageRating + ( 1 + bayesianWeight) * averageRatingForAllRooms;
	}
}
