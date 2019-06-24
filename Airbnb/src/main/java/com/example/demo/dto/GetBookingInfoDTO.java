package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class GetBookingInfoDTO {

    @NotBlank
    private String userNames;

    @NotEmpty
    private LocalDate startDate;

    @NotEmpty
    private LocalDate endDate;
}
