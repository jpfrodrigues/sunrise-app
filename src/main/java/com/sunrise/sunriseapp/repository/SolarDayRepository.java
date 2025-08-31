package com.sunrise.sunriseapp.repository;

import com.sunrise.sunriseapp.entity.SolarDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SolarDayRepository extends JpaRepository<SolarDay, Long> {
    Optional<SolarDay> findByLocationIgnoreCaseAndDate(String location, LocalDate date);
    List<SolarDay> findByLocationIgnoreCaseAndDateBetweenOrderByDateAsc(String location, LocalDate start, LocalDate end);
}
