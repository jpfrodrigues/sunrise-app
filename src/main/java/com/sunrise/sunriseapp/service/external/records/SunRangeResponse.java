package com.sunrise.sunriseapp.service.external.records;

import java.util.List;

public record SunRangeResponse(
        List<SunResult> results,
        String status
) {
}