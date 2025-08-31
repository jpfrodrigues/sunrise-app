package com.sunrise.sunriseapp.controller;

import com.sunrise.sunriseapp.dto.SolarDayDTO;
import com.sunrise.sunriseapp.dto.SolarQueryRequest;
import com.sunrise.sunriseapp.service.SolarService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/solar")
public class SolarController {

    private final SolarService service;

    public SolarController(SolarService service) {
        this.service = service;
    }

    // Ex.: GET /api/solar?location=Lisbon&start=2025-08-01&end=2025-08-05
    @GetMapping
    public List<SolarDayDTO> get(
            @RequestParam String location,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        return service.getRange(location, start, end);
    }

    // Alternativa em POST com body JSON
    @PostMapping
    public List<SolarDayDTO> post(@RequestBody @Valid SolarQueryRequest req) {
        return service.getRange(req.location(), req.start(), req.end());
    }
}

