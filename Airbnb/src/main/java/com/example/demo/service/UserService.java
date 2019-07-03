package com.example.demo.service;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.dto.*;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.Room;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserRepository;
import com.example.demo.exceptions.SignUpException;
import com.example.demo.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

	public List<User> getAllUsers(){
		return userRepository.findAll().stream().collect(Collectors.toList());
	}

	public User getUserById(long id) throws ElementNotFoundException {
		return userRepository.findById(id).orElseThrow(() -> new ElementNotFoundException("User not found"));
	}

	public GetUserProfileDTO convertUserToDTO(User user) throws ElementNotFoundException {
		return new GetUserProfileDTO(user.viewAllNames(), user.getPhone(),
				roomService.getUserRooms(user.getId()).stream().map(room -> roomService.convertRoomToDTO(room)).collect(Collectors.toList()),
				reviewService.getAllReviewsForUser(user.getId()).stream().map(review -> reviewService.convertReviewToDTO(review)).collect(Collectors.toList()));
	}

	public void signUp(SignUpDTO signUpDTO, HttpSession session) throws BadRequestException, SignUpException, NoSuchAlgorithmException, UnsupportedEncodingException {
		if (session.getAttribute("userId") != null) {
			throw new BadRequestException("User is already logged in");
		}
		if ( !this.isPasswordValid(signUpDTO.getPassword()) || !this.isValidEmailAddress(signUpDTO.getEmail())) {
			throw new SignUpException("Invalid email or password");
		}
		if (userRepository.findByEmail(signUpDTO.getEmail()).isPresent()) {
			throw new SignUpException("Email is already used");
		}
		User user = new User(null, signUpDTO.getFirstName(), signUpDTO.getLastName(),UserService.encryptPassword(signUpDTO.getPassword()) , signUpDTO.getEmail(),
				signUpDTO.getBirthDate(), signUpDTO.getPhone(),null);
		saveUserToDB(user);
	}

	public User login(LoginDTO loginDTO, HttpSession session) throws BadRequestException, ElementNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException {
		if (session.getAttribute("userId") != null) {
			throw new BadRequestException("User is already logged in!");
		}
		String encryptedPassword = UserService.encryptPassword(loginDTO.getPassword());
		User user = userRepository.findByEmailAndPassword(loginDTO.getEmail(), encryptedPassword).orElseThrow(() -> new ElementNotFoundException("User not found"));
		session.setAttribute("userId", user.getId());
		return user;
	}

	public void logout(HttpSession session) throws BadRequestException {
		if (session.getAttribute("userId") == null) {
			throw new BadRequestException("You must login first");
		}
		session.invalidate();
	}

	public User changeInformation(long userId, EditProfileDTO editProfileDTO) throws ElementNotFoundException, NoSuchAlgorithmException, UnsupportedEncodingException, BadRequestException {
		getUserById(userId);
		if ( !this.isPasswordValid(editProfileDTO.getPassword()) || !this.isValidEmailAddress(editProfileDTO.getEmail())) {
			throw new BadRequestException("Invalid email or password");
		}
		if (userRepository.findByEmail(editProfileDTO.getEmail()).isPresent()) {
			throw new BadRequestException("Email is already used");
		}

		User user = new User(userId, editProfileDTO.getFirstName(),editProfileDTO.getLastName(),UserService.encryptPassword(editProfileDTO.getPassword()),editProfileDTO.getEmail(),
				editProfileDTO.getBirthDate(),editProfileDTO.getPhone(),null);
		saveUserToDB(user);
		return user;
	}

	public List<Room> viewFavouriteRooms(long userId) throws ElementNotFoundException {
		User user = getUserById(userId);
		return user.getFavourites();
	}

	private boolean isValidEmailAddress(String email) {
		String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
	}

	// digit, lowercase, uppercase, at least 8 characters
	private boolean isPasswordValid(String password) {
		String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}";
		return password.matches(pattern);
	}

	private static String encryptPassword(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest crypt = MessageDigest.getInstance("SHA-1");
		crypt.reset();
		crypt.update(password.getBytes("UTF-8"));
		return new BigInteger(1, crypt.digest()).toString(16);
	}

	public static long authentication(HttpServletRequest request) throws UnauthorizedException {
		HttpSession session = request.getSession();
		if (session.getAttribute("userId") == null) {
			throw new UnauthorizedException("You must login first");
		}
		return (long) session.getAttribute("userId");
	}
}