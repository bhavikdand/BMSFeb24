package com.example.bmsfeb24.services;

import com.example.bmsfeb24.exceptions.SeatNotAvailableException;
import com.example.bmsfeb24.exceptions.UserNotFoundException;
import com.example.bmsfeb24.models.*;
import com.example.bmsfeb24.repositories.SeatTypeShowRepository;
import com.example.bmsfeb24.repositories.ShowSeatRepository;
import com.example.bmsfeb24.repositories.TicketRepository;
import com.example.bmsfeb24.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private UserRepository userRepository;
    private ShowSeatRepository showSeatRepository;
    private SeatTypeShowRepository seatTypeShowRepository;
    private TicketRepository ticketRepository;

    @Autowired
    public TicketService(UserRepository userRepository, ShowSeatRepository showSeatRepository, SeatTypeShowRepository seatTypeShowRepository, TicketRepository ticketRepository) {
        this.userRepository = userRepository;
        this.showSeatRepository = showSeatRepository;
        this.seatTypeShowRepository = seatTypeShowRepository;
        this.ticketRepository = ticketRepository;
    }

    public Ticket bookTicket(int userId, List<Integer> showSeatIds) throws UserNotFoundException, SeatNotAvailableException{
        Optional<User> userOptional = this.userRepository.findById(userId);
        if(userOptional.isEmpty()){
            throw new UserNotFoundException("User not found");
        }
        User user = userOptional.get();
        List<ShowSeat> showSeats = checkAndBlockSeats(user, showSeatIds);

        Ticket ticket = new Ticket();
        ticket.setUser(user);
        Show show = showSeats.get(0).getShow();
        ticket.setShow(show);
        // I have a list of showseats, I want a list of seats
        // I want to do it using streams
        List<Seat> seats = showSeats.stream()
                .map(showSeat -> showSeat.getSeat())
                .toList();

        ticket.setSeats(seats);
        ticket.setTimeOfBooking(new Date());
        ticket.setPaymentStatus(PaymentStatus.PENDING);

        List<SeatTypeShow> seatTypeShows = seatTypeShowRepository.findByShow(show);
        double totalPrice = 0;
        Map<SeatType, Double> pricesMap = new EnumMap<>(SeatType.class);

        for(SeatTypeShow seatTypeShow: seatTypeShows){
            pricesMap.put(seatTypeShow.getSeatType(), seatTypeShow.getPrice());
        }

        for(Seat seat: seats){
            totalPrice += pricesMap.get(seat.getSeatType());
        }
        ticket.setPrice(totalPrice);
        return this.ticketRepository.save(ticket);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public List<ShowSeat> checkAndBlockSeats(User user, List<Integer> showSeatIds) throws SeatNotAvailableException{
        List<ShowSeat> avlShowSeats = this.showSeatRepository.findShowSeatsByIdInAndSeatStatus_Available(showSeatIds);

        if(avlShowSeats.size() == showSeatIds.size()){

            for(ShowSeat ss: avlShowSeats){
                ss.setSeatStatus(SeatStatus.BLOCKED);
                ss.setUser(user);
            }
            showSeatRepository.saveAll(avlShowSeats);
            return avlShowSeats;
        }
        else {
            throw new SeatNotAvailableException("Some/all of the requested seats are not available");
        }


    }
}
