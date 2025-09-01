package com.sunrise.sunriseapp.service;

import com.sunrise.sunriseapp.dto.SolarDayDTO;
import com.sunrise.sunriseapp.entity.SolarDay;
import com.sunrise.sunriseapp.exception.InvalidLocationException;
import com.sunrise.sunriseapp.repository.SolarDayRepository;
import com.sunrise.sunriseapp.service.external.records.Coordinates;
import com.sunrise.sunriseapp.service.external.GeoCodingClient;
import com.sunrise.sunriseapp.service.external.records.SunApiDayResult;
import com.sunrise.sunriseapp.service.external.SunriseSunsetClient;
import com.sunrise.sunriseapp.service.external.records.SunRangeResponse;
import com.sunrise.sunriseapp.service.external.records.SunResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SolarService {

    private final SolarDayRepository repo;
    private final SunriseSunsetClient client;

    private final GeoCodingClient geoCoding;

    public SolarService(SolarDayRepository repo, SunriseSunsetClient client, GeoCodingClient geoCoding) {
        this.repo = repo;
        this.client = client;
        this.geoCoding = geoCoding;
    }

    @Transactional
    public List<SolarDayDTO> getRange(String location, LocalDate start, LocalDate end) {
        validate(location, start, end);

        // Convert Location to Coordenates
        Coordinates coords = geoCoding.getCode(location);

        if (coords == null) {
            throw new InvalidLocationException("Localização invalida ou não encontrada: " + location);
        }

        System.out.println("Coordenadas " + location + " = " + coords.toString());

        // Search on BD
        List<LocalDate> days = start.datesUntil(end.plusDays(1)).toList();

        Map<LocalDate, SolarDay> existing = repo
                .findByLocationIgnoreCaseAndDateBetweenOrderByDateAsc(location, start, end)
                .stream().collect(Collectors.toMap(SolarDay::getDate, d -> d));

        List<LocalDate> missingDays = days.stream().filter(d -> !existing.containsKey(d)).toList();

        // Dias em falta

        Map<LocalDate, SunResult> apiByDate = Collections.emptyMap();
        if (!missingDays.isEmpty()) {
            LocalDate missStart = missingDays.get(0);
            LocalDate missEnd   = missingDays.get(missingDays.size()-1);
            apiByDate = client.fetchRange(coords.lat(), coords.lon(), missStart, missEnd);
        }
        List<SolarDay> results = new ArrayList<>();

        for (LocalDate d : missingDays) {
            SunResult res = apiByDate.get(d);
            if (res == null) continue;

            OffsetDateTime sunriseUtc = res.sunrise() == null ? null : OffsetDateTime.ofInstant(Instant.ofEpochSecond(res.sunrise()), ZoneOffset.UTC);
            OffsetDateTime sunsetUtc  = res.sunset()  == null ? null : OffsetDateTime.ofInstant(Instant.ofEpochSecond(res.sunset()),  ZoneOffset.UTC);
            OffsetDateTime goldenStart = res.golden_hour() == null ? null : OffsetDateTime.ofInstant(Instant.ofEpochSecond(res.golden_hour()), ZoneOffset.UTC);

            SolarDay saved = SolarDay.builder()
                    .location(location.trim())
                    .date(d)
                    .sunriseUtc(sunriseUtc)
                    .sunsetUtc(sunsetUtc)
                    .goldenHourStartUtc(goldenStart)
                    .goldenHourEndUtc(null)
                    .sunNeverSets(sunsetUtc == null)
                    .sunNeverRises(sunriseUtc == null)
                    .source("API")
                    .createdAt(Instant.now())
                    .build();
            repo.save(saved);
            existing.put(d,saved);
        }

        return existing.values().stream()
                .sorted(Comparator.comparing(SolarDay::getDate))
                .map(s -> new SolarDayDTO(
                        s.getLocation(),
                        s.getDate(),
                        s.getSunriseUtc(),
                        s.getSunsetUtc(),
                        s.getGoldenHourStartUtc(),
                        s.getGoldenHourEndUtc(),
                        s.getSunNeverSets(),
                        s.getSunNeverRises(),
                        "DB"
                )).toList();
    }

    private void validate(String location, LocalDate start, LocalDate end) {
        if (location == null || location.isBlank())
            throw new InvalidLocationException("O parâmetro 'location' é obrigatório.");
        if (start == null || end == null)
            throw new IllegalArgumentException("Os parâmetros 'start' e 'end' são obrigatórios.");
        if (end.isBefore(start))
            throw new IllegalArgumentException("'end' não pode ser anterior a 'start'.");
        if (Duration.between(start.atStartOfDay(), end.plusDays(1).atStartOfDay()).toDays() > 31)
            throw new IllegalArgumentException("O intervalo máximo suportado nesta versão é de 31 dias.");
    }
}

