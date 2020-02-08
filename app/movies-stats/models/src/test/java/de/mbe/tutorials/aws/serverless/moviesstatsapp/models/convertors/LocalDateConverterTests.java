package de.mbe.tutorials.aws.serverless.moviesstatsapp.models.convertors;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class LocalDateConverterTests {

    @Test
    void conversionWorksFromLocalDateToString() {
        assertEquals("1985-12-24", LocalDateConverter.convertToString(LocalDate.of(1985, 12, 24)));
        assertEquals("2019-06-01", LocalDateConverter.convertToString(LocalDate.of(2019, 6, 1)));
    }

    @Test
    void conversionWorksFromStringToLocalDate() {
        assertEquals(LocalDate.of(1985, 12, 24), LocalDateConverter.parseString("1985-12-24"));
        assertEquals(LocalDate.of(2019, 6, 1), LocalDateConverter.parseString("2019-06-01"));
    }
}
