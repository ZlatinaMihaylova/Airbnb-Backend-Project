package com.example.demo.controllers;

import com.example.demo.dto.RoomBookingDTO;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.BookingIsOverlapingException;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.service.BookingService;
import com.example.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/rooms/booking")
    public long makeReservation(@RequestBody RoomBookingDTO reservation, HttpServletRequest request) throws ElementNotFoundException, UnauthorizedException, BookingIsOverlapingException, BadRequestException {
        long id = UserController.authentication(request);
        return bookingService.makeReservation(reservation, id);
    }

    @GetMapping("/rooms/bookings={roomId}")
    public Set<RoomBookingDTO> getAllBookingsForRoom(@PathVariable long roomId) throws ElementNotFoundException{
        return bookingService.getAllBookingsForRoom(roomId).stream().map(booking -> BookingService.convertBookingToDTO(booking)).collect(Collectors.toSet());
    }
}
