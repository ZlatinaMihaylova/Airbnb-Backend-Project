package com.example.demo.controllers;

import com.example.demo.dto.AddBookingDTO;
import com.example.demo.dto.GetBookingInfoDTO;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.BookingIsOverlapingException;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.Booking;
import com.example.demo.service.BookingService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/rooms/roomId={roomId}/bookings")
    public ModelAndView makeReservation(@PathVariable long roomId, @RequestBody @Valid AddBookingDTO addBookingDTO, HttpServletRequest request) throws ElementNotFoundException, UnauthorizedException, BookingIsOverlapingException, BadRequestException {
        long userId = UserService.authentication(request);
        bookingService.makeReservation(roomId, addBookingDTO, userId);
        return new ModelAndView("redirect:/myBookings");
    }

    @GetMapping("/rooms/roomId={roomId}/bookings")
    public Set<GetBookingInfoDTO> getAllBookingsForRoom(@PathVariable long roomId) throws ElementNotFoundException{
        return bookingService.getAllBookingsForRoom(roomId).stream().map(booking -> bookingService.convertBookingToDTO(booking)).collect(Collectors.toSet());
    }
}
