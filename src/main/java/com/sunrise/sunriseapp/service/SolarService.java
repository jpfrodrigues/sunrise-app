package com.sunrise.sunriseapp.service;

import com.sunrise.sunriseapp.dto.SolarDayDTO;
import com.sunrise.sunriseapp.entity.SolarDay;
import com.sunrise.sunriseapp.exception.InvalidLocationException;
import com.sunrise.sunriseapp.repository.SolarDayRepository;
import com.sunrise.sunriseapp.service.external.SunApiDayResult;
import com.sunrise.sunriseapp.service.external.SunriseSunsetClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SolarService {

    private final SolarDayRepository repo;
    private final SunriseSunsetClient client;

    public SolarService(SolarDayRepository repo, SunriseSunsetClient client) {
        this.repo = repo;
        this.client = client;
    }

    @Transactional
    public List<SolarDayDTO> getRange(String location, LocalDate start, LocalDate end) {
        validate(location, start, end);

        List<LocalDate> days = start.datesUntil(end.plusDays(1)).toList();
        // 1) tenta BD


        Map<LocalDate, SolarDay> existing = repo
                .findByLocationIgnoreCaseAndDateBetweenOrderByDateAsc(location, start, end)
                .stream().collect(Collectors.toMap(SolarDay::getDate, d -> d));

        List<SolarDay> results = new ArrayList<>();

        for (LocalDate d : days) {
            SolarDay fromDb = existing.get(d);
            if (fromDb != null) {
                results.add(fromDb);
                System.out.print("Test: Jose Pedro");
                continue;
            }
            // 2) chama API externa para o dia em falta
            SunApiDayResult api = client.fetchDay(location, d);

            SolarDay saved = SolarDay.builder()
                    .location(location.trim())
                    .date(d)
                    .sunriseUtc(api.sunriseUtc())
                    .sunsetUtc(api.sunsetUtc())
                    .goldenHourStartUtc(api.goldenHourStartUtc())
                    .goldenHourEndUtc(api.goldenHourEndUtc())
                    .sunNeverSets(api.sunNeverSets())
                    .sunNeverRises(api.sunNeverRises())
                    .source("API")
                    .createdAt(Instant.now())
                    .build();
            repo.save(saved);
            results.add(saved);
        }

        return results.stream()
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
                        s.getSource()
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

