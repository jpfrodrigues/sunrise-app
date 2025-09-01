package com.sunrise.sunriseapp.service.external;

import com.sunrise.sunriseapp.exception.ExternalApiException;
import com.sunrise.sunriseapp.service.external.records.SunApiDayResult;
import com.sunrise.sunriseapp.service.external.records.SunRangeResponse;
import com.sunrise.sunriseapp.service.external.records.SunResult;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class SunriseSunsetClient {

    private final WebClient client;

    public SunriseSunsetClient(WebClient sunClient) {
        this.client = sunClient;
    }

    public SunApiDayResult fetchDay(String location, LocalDate date) {


        if (location.isBlank()) throw new ExternalApiException("Empty location");
        OffsetDateTime sr = date.atStartOfDay(ZoneOffset.UTC).plusHours(6).toOffsetDateTime();
        OffsetDateTime ss = date.atStartOfDay(ZoneOffset.UTC).plusHours(18).toOffsetDateTime();
        return new SunApiDayResult(
                sr,
                ss,
                sr.minusMinutes(30),
                sr.plusMinutes(30),
                false,
                false
        );
    }

    public Map<LocalDate, SunResult> fetchRange(double lat, double lon, LocalDate start, LocalDate end){
        String uri = UriComponentsBuilder.fromPath("/json")
                .queryParam("lat", lat)
                .queryParam("lng", lon)
                .queryParam("date_start", start.toString())
                .queryParam("date_end", end.toString())
                .queryParam("timezone", "UTC")      // devolve tempos relativos a UTC
                .queryParam("time_format", "unix")  // devolve epoch (UTC)
                .build(true).toUriString();

        SunRangeResponse resp = client.get().uri(uri)
                .retrieve()
                .bodyToMono(SunRangeResponse.class)
                .block();

        if (resp == null || resp.results() == null) {
            throw new ExternalApiException("Resposta vazia da SunriseSunset API");
        }

        Map<LocalDate, SunResult> byDate = new HashMap<>();
        for (SunResult r : resp.results()) {
            LocalDate d = LocalDate.parse(r.date());
            byDate.put(d, r);
        }
        return byDate;
    }
}

