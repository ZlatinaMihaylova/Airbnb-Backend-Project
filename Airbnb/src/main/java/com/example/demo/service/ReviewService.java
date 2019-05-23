package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ReviewRepository;
import com.example.demo.dao.RoomRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.dto.ReviewsForRoomDTO;
import com.example.demo.dto.WriteReviewDTO;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.Review;

@Service
public class ReviewService {

	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Autowired
	private RoomRepository roomRepository;
	
	public Set<ReviewsForRoomDTO> getAllReviewsByRoomId(Long roomId) throws ElementNotFoundException {
		roomRepository.findById(roomId).orElseThrow(() -> new ElementNotFoundException("Room not found"));
		
		Set<ReviewsForRoomDTO> allReviewsForRoom = new TreeSet<ReviewsForRoomDTO>(new Comparator<ReviewsForRoomDTO>() {

			@Override
			public int compare(ReviewsForRoomDTO o1, ReviewsForRoomDTO o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
			
		});
		
		allReviewsForRoom = ( reviewRepository.findByRoomId(roomId)
				.stream()
				.map(review -> new ReviewsForRoomDTO(review.getUser().viewAllNames(), review.getDate(), review.getText())).collect(Collectors.toSet()));
		
		return allReviewsForRoom;
	}
	
	public void addReviewForRoom(long userId,long roomId, WriteReviewDTO reviewDTO) throws ElementNotFoundException, UnauthorizedException {
		LocalDateTime time = LocalDateTime.now();

		if ( roomRepository.findById(roomId).get().getUserId() == userId) {
			throw new UnauthorizedException("User can not add review for his own room!");		
		}
		reviewRepository.saveAndFlush(new Review(null,time,reviewDTO.getText(),
				userRepository.findById(userId).orElseThrow(() -> new ElementNotFoundException("User not found!")),
				roomRepository.findById(roomId).orElseThrow(() -> new ElementNotFoundException("Room not found")), reviewDTO.getStars()));
	}
	
	public double getRoomRating(long roomId)   {
	//	roomRepository.findById(roomId).orElseThrow(() -> new ElementNotFoundException("Room not found"));
		return reviewRepository.findByRoomId(roomId).stream()
				.mapToInt( review -> review.getStars()).average().orElse(0);

	}
	
	public int getRoomTimesRated(long roomId)   {
	//	roomRepository.findById(roomId).orElseThrow(() -> new ElementNotFoundException("Room not found"));
		return (int) reviewRepository.findByRoomId(roomId).stream().count();
	}

	public void removeAllReviewsForRoom(long roomId) {
		Set<Review> reviewsForRoom = reviewRepository.findByRoomId(roomId).stream()
		.collect(Collectors.toSet());
		
		reviewRepository.deleteAll(reviewsForRoom);
	}

}
