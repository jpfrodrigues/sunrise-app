CREATE TABLE IF NOT EXISTS solar_day (
    id BIGSERIAL PRIMARY KEY,
    location VARCHAR(120) NOT NULL,
    obs_date DATE NOT NULL,
    sunrise_utc TIMESTAMPTZ,
    sunset_utc  TIMESTAMPTZ,
    golden_hour_start_utc TIMESTAMPTZ,
    golden_hour_end_utc   TIMESTAMPTZ,
    sun_never_sets BOOLEAN,
    sun_never_rises BOOLEAN,
    source VARCHAR(16) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_location_date UNIQUE (location, obs_date)
    );

-- indices úteis para queries por intervalo
CREATE INDEX IF NOT EXISTS idx_solar_day_loc_date ON solar_day (location, obs_date);
