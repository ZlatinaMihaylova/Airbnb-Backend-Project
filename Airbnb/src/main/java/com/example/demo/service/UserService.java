package com.example.demo.service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.BookingRepository;
import com.example.demo.dao.RoomRepository;
import com.example.demo.dao.UserRepository;
import com.example.demo.dto.BookingListDTO;
import com.example.demo.dto.EditProfileDTO;
import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.ReviewsForRoomDTO;
import com.example.demo.dto.RoomListDTO;
import com.example.demo.dto.UserBookingsDTO;
import com.example.demo.dto.UserProfileDTO;
import com.example.demo.exceptions.SignUpException;
import com.example.demo.exceptions.UserException;
import com.example.demo.model.Room;
import com.example.demo.model.User;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoomService roomService;
	
	@Autowired
	private ReviewService reviewService;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	private static String encryptPassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {

	    MessageDigest crypt = MessageDigest.getInstance("SHA-1");
	    crypt.reset();
	    crypt.update(password.getBytes("UTF-8"));

	    return new BigInteger(1, crypt.digest()).toString(16);
	}
	
	public Set<User> getAllUsers(){
		return userRepository.findAll().stream().collect(Collectors.toSet());

	}
	
	public long signup(User user) throws SignUpException, NoSuchAlgorithmException, UnsupportedEncodingException {
		
		if ( !this.isPasswordValid(user.getPassword()) || !this.isValidEmailAddress(user.getEmail())) {
			throw new SignUpException("Invalid email or password");
		}
		
		if (userRepository.findAll().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
			throw new SignUpException("Email is already used");
		}
		
		User result = new User(null, user.getFirstName(), user.getLastName(),UserService.encryptPassword(user.getPassword()) , user.getEmail(),
				user.getBirthDate(), user.getPhone(),null);
		
		userRepository.saveAndFlush(result);
		
		return result.getId();
	}
	
	public UserProfileDTO getUserById(long userId) throws UserException {
		User user = userRepository.findById(userId);
		
		if ( user == null) {
			throw new UserException("User not found");
		}
		
		return new UserProfileDTO(user.viewAllNames(),user.getPhone(),roomService.getUserRooms(userId),roomService.getUserReviews(userId));
	}
	
	public User login(LoginDTO loginDTO) throws UserException, NoSuchAlgorithmException, UnsupportedEncodingException {
		User user = null;
		String encryptedPassword = UserService.encryptPassword(loginDTO.getPassword());
		try{
			 user = userRepository.findAll().stream().filter(u -> (u.getEmail().equals(loginDTO.getEmail()) && u.getPassword().equals(encryptedPassword))).findFirst().get();
		}
		catch(NoSuchElementException e) {
			throw new UserException("User not found");
		}
	
		return user;
	}
	
	public UserProfileDTO changeInformation(long userId, EditProfileDTO editProfileDTO) throws UserException, NoSuchAlgorithmException, UnsupportedEncodingException {
		User user = new User(userId, editProfileDTO.getFirstName(),editProfileDTO.getLastName(),UserService.encryptPassword(editProfileDTO.getPassword()),editProfileDTO.getEmail(),
				editProfileDTO.getBirthDate(),editProfileDTO.getPhone(),null);
		userRepository.save(user);
		return this.getUserById(userId);
	}
	
	public List<RoomListDTO> viewFavouritesRoom(long userId){
		return userRepository
				.findById(userId)
				.getFavourites()
				.stream()
				.map(room -> new RoomListDTO(room.getDetails(), room.getCity().getName(), reviewService.getRoomRating(room.getId()), reviewService.getRoomTimesRated(room.getId())))
				.collect(Collectors.toList());
	}
	
	public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
	}
	
	// digit, lowercase, uppercase, at least 8 characters
	public boolean isPasswordValid(String password) {
	    String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}";
	    return password.matches(pattern);
	  }


	public Set<UserBookingsDTO> showMyBookings(long userId) {
			return bookingRepository.findAll().stream()
			.filter(b -> b.getUser().getId().equals(userId))
			.map(b -> new UserBookingsDTO(b.getRoom().getId(), b.getStartDate(), b.getEndDate()))
			.collect(Collectors.toSet());
	}
}