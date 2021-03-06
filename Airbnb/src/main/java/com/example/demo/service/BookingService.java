package com.example.demo.service;

import com.example.demo.dao.BookingRepository;
import com.example.demo.dto.AddBookingDTO;
import com.example.demo.dto.GetBookingInfoDTO;
import com.example.demo.dto.SendMessageDTO;
import com.example.demo.exceptions.BadRequestException;
import com.example.demo.exceptions.BookingIsOverlapingException;
import com.example.demo.exceptions.ElementNotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.model.Booking;
import com.example.demo.model.Room;
import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private MessageService messageService;

    public List<Booking> getAllUsersBookings(User user) {
        return bookingRepository.findByUserId(user.getId());
    }

    public void makeReservation(Room room, AddBookingDTO reservation, User user) throws BookingIsOverlapingException, UnauthorizedException, BadRequestException {
        if ( room.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("User can not book hiw own room!");
        }
        Booking result = new Booking(null, reservation.getStartDate(), reservation.getEndDate(),
                user, room);
        validateReservationDates(result);
        checkForOverlappingDates(result);
        bookingRepository.saveAndFlush(result);
    }

    public void removeAllBookingsFromRoom(Room room,User user) throws UnauthorizedException {
        List<Booking> bookings = bookingRepository.findByRoomId(room.getId());
        for ( Booking booking : bookings) {
            if ( booking.getStartDate().isAfter(LocalDate.now())) {
                messageService.sendMessage(user, booking.getUser(),
                        new SendMessageDTO("Your booking for " + booking.getRoom().getDetails() + " has been canceled. The room has been deleted"));
            }
        }
        bookingRepository.deleteAll(bookings);
    }

    public void removeBookingById(long bookingId) throws  ElementNotFoundException{
        bookingRepository.delete(bookingRepository.findById(bookingId).orElseThrow(() -> new ElementNotFoundException("Booking not found!")));
    }

    public List<Booking> getAllBookingsForRoom(long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    public GetBookingInfoDTO convertBookingToDTO(Booking booking) {
        return new GetBookingInfoDTO(booking.getUser().viewAllNames(), booking.getStartDate(), booking.getEndDate(), roomService.convertRoomToDTO(booking.getRoom()));
    }

    private void validateReservationDates(Booking reservation) throws BadRequestException {
        if (reservation.getStartDate().isAfter(reservation.getEndDate())) {
            LocalDate temp = reservation.getStartDate();
            reservation.setStartDate(reservation.getEndDate());
            reservation.setEndDate(temp);
        }
        if ( reservation.getStartDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("User can book only for dates after today!");
        }
    }

    private void checkForOverlappingDates(Booking reservation) throws BookingIsOverlapingException {
        boolean isOverlapping = bookingRepository.findByRoomId(reservation.getRoom().getId()).stream()
                .anyMatch(booking -> booking.overlap(reservation.getStartDate(),reservation.getEndDate()));

        if(isOverlapping) {
            throw new BookingIsOverlapingException("Overlapping dates");
        }
    }
}
