package com.sunrise.sunriseapp.service.external;

import java.time.OffsetDateTime;

// Podes ajustar estes modelos conforme a resposta real da API.
public record SunApiDayResult(
        OffsetDateTime sunriseUtc,
        OffsetDateTime sunsetUtc,
        OffsetDateTime goldenHourStartUtc,
        OffsetDateTime goldenHourEndUtc,
        boolean sunNeverSets,
        boolean sunNeverRises
) {}
