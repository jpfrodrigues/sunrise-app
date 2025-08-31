package com.sunrise.sunriseapp.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record SolarDayDTO(
        String location,
        LocalDate date,
        OffsetDateTime sunriseUtc,
        OffsetDateTime sunsetUtc,
        OffsetDateTime goldenHourStartUtc,
        OffsetDateTime goldenHourEndUtc,
        Boolean sunNeverSets,
        Boolean sunNeverRises,
        String source
) {}
