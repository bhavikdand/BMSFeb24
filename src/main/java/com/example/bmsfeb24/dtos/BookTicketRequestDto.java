package com.example.bmsfeb24.dtos;

import lombok.Data;

import java.util.List;

@Data
public class BookTicketRequestDto {
    private int userId;
    private List<Integer> showSeatIds;
}
