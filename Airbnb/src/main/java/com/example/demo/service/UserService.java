package com.example.demo.service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserRepository;
import com.example.demo.dto.EditProfileDTO;
import com.example.demo.dto.LoginDTO;
import com.example.demo.dto.RoomListDTO;
import com.example.demo.dto.UserProfileDTO;
import com.example.demo.exceptions.SignUpException;
import com.example.demo.model.User;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BookingService bookingService;
	
	@Autowired
	private RoomService roomService;
	
	@Autowired
	private ReviewService reviewService;


	public void saveUserToDB(User user) {
		userRepository.saveAndFlush(user);
	}

	public Set<User> getAllUsers(){
		return userRepository.findAll().stream().collect(Collectors.toSet());
	}

	public User findById(long id) throws ElementNotFoundException {
		return userRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("User not found"));
	}

	public UserProfileDTO convertUserToDTO(User user) throws ElementNotFoundException {
		return new UserProfileDTO(user.viewAllNames(), user.getPhone(),
				roomService.getUserRooms(user.getId()).stream().map(room -> roomService.convertRoomToDTO(room)).collect(Collectors.toList()),
				reviewService.getAllReviewsForUser(user.getId()).stream().map(review -> reviewService.convertReviewToDTO(review)).collect(Collectors.toList()));
	}
	
	public void signUp(User user) throws SignUpException, NoSuchAlgorithmException, UnsupportedEncodingException {
		if ( !this.isPasswordValid(user.getPassword()) || !this.isValidEmailAddress(user.getEmail())) {
			throw new SignUpException("Invalid email or password");
		}
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new SignUpException("Email is already used");
		}
		User result = new User(null, user.getFirstName(), user.getLastName(),UserService.encryptPassword(user.getPassword()) , user.getEmail(),
				user.getBirthDate(), user.getPhone(),null);
		saveUserToDB(result);
	}

	public User login(LoginDTO loginDTO) throws ElementNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {
		String encryptedPassword = UserService.encryptPassword(loginDTO.getPassword());
		return userRepository.findByEmailAndPassword(loginDTO.getEmail(), encryptedPassword).orElseThrow(() -> new ElementNotFoundException("User not found"));
	}
	
	public User changeInformation(long userId, EditProfileDTO editProfileDTO) throws ElementNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {
		User user = new User(userId, editProfileDTO.getFirstName(),editProfileDTO.getLastName(),UserService.encryptPassword(editProfileDTO.getPassword()),editProfileDTO.getEmail(),
				editProfileDTO.getBirthDate(),editProfileDTO.getPhone(),null);
		saveUserToDB(user);
		return user;
	}

	public List<Room> viewFavouriteRooms(long userId) throws ElementNotFoundException {
		User user = findById(userId);
		return user.getFavourites();
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

	private static String encryptPassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest crypt = MessageDigest.getInstance("SHA-1");
		crypt.reset();
		crypt.update(password.getBytes("UTF-8"));
		return new BigInteger(1, crypt.digest()).toString(16);
	}
}