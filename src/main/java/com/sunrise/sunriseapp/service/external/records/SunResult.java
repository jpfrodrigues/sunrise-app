package com.sunrise.sunriseapp.service.external.records;

public record SunResult(
        String date,
        Long sunrise,     // epoch seconds UTC (pode ser null em casos polares)
        Long sunset,      // idem
        Long first_light,
        Long last_light,
        Long dawn,
        Long dusk,
        Long solar_noon,
        Long golden_hour, // a API devolve um único instante "golden_hour"
        String day_length,
        String timezone,
        Integer utc_offset
) {
}

