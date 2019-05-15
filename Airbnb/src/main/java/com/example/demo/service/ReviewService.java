package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ReviewRepository;
import com.example.demo.dao.RoomRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.dto.ReviewsForRoomDTO;
import com.example.demo.dto.RoomListDTO;
import com.example.demo.dto.WriteReviewDTO;
import com.example.demo.exceptions.RoomNotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.exceptions.UserException;
import com.example.demo.model.Message;
import com.example.demo.model.Review;

@Service
public class ReviewService {

	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private RoomRepository roomRepository;
	
	public Set<ReviewsForRoomDTO> getAllReviewsByRoomId(Long roomId) throws RoomNotFoundException {
		if ( roomRepository.findById(roomId) == null) {
			throw new RoomNotFoundException("Room not found");
		}
		
		Set<ReviewsForRoomDTO> allReviewsForRoom = new TreeSet<ReviewsForRoomDTO>(new Comparator<ReviewsForRoomDTO>() {

			@Override
			public int compare(ReviewsForRoomDTO o1, ReviewsForRoomDTO o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
			
		});
		
		allReviewsForRoom = ( reviewRepository.findAll()
				.stream()
				.map(review -> new ReviewsForRoomDTO(review.getUser().viewAllNames(), review.getDate(), review.getText())).collect(Collectors.toSet()));
		
		return allReviewsForRoom;
	}
	
	public void addReviewForRoom(long userId,long roomId, WriteReviewDTO reviewDTO) throws RoomNotFoundException, UnauthorizedException {
		LocalDateTime time = LocalDateTime.now();
		
		if ( roomRepository.findById(roomId) == null) {
			throw new RoomNotFoundException("Room not found");
		}
		if ( roomRepository.findById(roomId).getUserId() == userId) {
			throw new UnauthorizedException("User can not add review for his own room!");		
		}
		Review review = new Review(null,time,reviewDTO.getText(),userRepository.findById(userId),roomRepository.findById(roomId), reviewDTO.getStars());
		
		
		reviewRepository.saveAndFlush(review);
	}
	
	public double getRoomRating(long roomId) {
		return reviewRepository.findAll().stream().filter(review -> review.getRoom().equals(roomRepository.findById(roomId)))
				.mapToInt( review -> review.getStars()).average().orElse(0);

	}
	
	public int getRoomTimesRated(long roomId) {
		return (int) reviewRepository.findAll().stream().filter(review -> review.getRoom().equals(roomRepository.findById(roomId))).count();
	}

	public void removeAllReviewsForRoom(long roomId) {
		Set<Review> reviewsForRoom = reviewRepository.findAll().stream()
		.filter(r -> r.getRoom().getId().equals(roomId))
		.collect(Collectors.toSet());
		
		reviewRepository.deleteAll(reviewsForRoom);
	}

}
