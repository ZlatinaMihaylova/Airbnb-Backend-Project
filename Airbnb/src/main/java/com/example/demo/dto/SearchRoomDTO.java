package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class SearchRoomDTO {

    @NotEmpty
    private String city;

    @Future
    private LocalDate startDate;

    @Future
    private LocalDate endDate;

    @Positive
    private int guests;
}
