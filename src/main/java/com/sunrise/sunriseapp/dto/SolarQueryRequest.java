package com.sunrise.sunriseapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record SolarQueryRequest(
        @NotBlank String location,
        @NotNull LocalDate start,
        @NotNull LocalDate end
) {}