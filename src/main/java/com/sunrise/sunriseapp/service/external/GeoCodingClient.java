package com.sunrise.sunriseapp.service.external;

import com.sunrise.sunriseapp.service.external.records.Coordinates;
import com.sunrise.sunriseapp.service.external.records.GeoResult;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
public class GeoCodingClient {

    private final WebClient geocodeClient;

    public GeoCodingClient(WebClient geocodeClient) {
        this.geocodeClient = geocodeClient;
    }

    public Coordinates getCode(String location) {
        String uri = UriComponentsBuilder.fromPath("/search")
                .queryParam("q", location)
                .queryParam("format", "jsonv2")
                .queryParam("limit", 1)
                .build(true).toUriString();

        System.out.println(uri);

        List<GeoResult> results = geocodeClient.get().uri(uri).retrieve()
                .bodyToFlux(GeoResult.class)
                .collectList()
                .block();

        if (results == null || results.isEmpty()) {
            return null;
        }
        GeoResult r = results.get(0);
        return new Coordinates(r.lat(), r.lon(), r.display_name());
    }


}
