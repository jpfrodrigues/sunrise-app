package com.sunrise.sunriseapp.service.external;

import com.sunrise.sunriseapp.exception.ExternalApiException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.*;

@Component
public class SunriseSunsetClient {

    private final WebClient client;

    public SunriseSunsetClient(WebClient sunClient) {
        this.client = sunClient;
    }

    public SunApiDayResult fetchDay(String location, LocalDate date) {
        // TODO: geocoding (location -> lat/lon). Por enquanto, mock controlado para manter o fluxo.
        // Lógica real (exemplo):
        // var uri = UriComponentsBuilder.fromPath("/json")
        //      .queryParam("lat", lat)
        //      .queryParam("lng", lon)
        //      .queryParam("date", date.toString())
        //      .build().toUriString();
        //
        // var resp = client.get().uri(uri).retrieve()...
        //
        // Tratamento de casos polares e erros.

        // MOCK TEMPORÁRIO (remove assim que ligarmos a API real):
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
}

