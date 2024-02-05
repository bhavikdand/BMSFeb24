package com.example.bmsfeb24.controllers;

import com.example.bmsfeb24.dtos.BookTicketRequestDto;
import com.example.bmsfeb24.dtos.BookTicketResponseDto;
import com.example.bmsfeb24.models.Ticket;
import com.example.bmsfeb24.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class TicketController {

    private TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public BookTicketResponseDto bookTicket(BookTicketRequestDto requestDto){
        BookTicketResponseDto responseDto = new BookTicketResponseDto();
        try {
            Ticket ticket = ticketService.bookTicket(requestDto.getUserId(), requestDto.getShowSeatIds());
            responseDto.setTicket(ticket);
            return responseDto;
        } catch (Exception e){
            return responseDto;
        }
    }
}
