package de.mbe.tutorials.aws.serverless.moviesstatsapp.models.convertors;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter implements DynamoDBTypeConverter<String, LocalDate> {

    public static final DateTimeFormatter ISO_LOCAL_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public String convert(final LocalDate object) {
        return convertToString(object);
    }

    @Override
    public LocalDate unconvert(final String object) {
        return parseString(object);
    }

    public static String convertToString(final LocalDate object) {
        return object.format(ISO_LOCAL_DATE_FORMATTER);
    }

    public static LocalDate parseString(final String object) {
        return LocalDate.parse(object, ISO_LOCAL_DATE_FORMATTER);
    }
}