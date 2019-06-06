package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class SearchRoomDTO {

    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private int guests;
}
