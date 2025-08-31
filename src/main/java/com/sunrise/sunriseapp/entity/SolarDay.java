package com.sunrise.sunriseapp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "solar_day",
        uniqueConstraints = @UniqueConstraint(name="uk_location_date", columnNames = {"location","obs_date"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SolarDay {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String location; // nome pedido pelo utilizador (Lisbon, Berlin, ...)

    @Column(name = "obs_date", nullable = false)
    private LocalDate date;

    //public LocalDate getDate() { return date; }

    // Guardamos UTC
    private OffsetDateTime sunriseUtc;
    private OffsetDateTime sunsetUtc;

    // “Golden hour” pode ser início e fim
    private OffsetDateTime goldenHourStartUtc;
    private OffsetDateTime goldenHourEndUtc;

    // Flags para casos polares
    private Boolean sunNeverSets; // dia polar
    private Boolean sunNeverRises; // noite polar

    @Column(nullable = false)
    private String source; // "DB" ou "API"

    @Column(nullable = false)
    private Instant createdAt;
}