package com.tlu.officehours.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts BookingStatus enum to/from database string values.
 * Handles case-insensitive reading for backward compatibility with Laravel data.
 */
@Converter(autoApply = true)
public class BookingStatusConverter implements AttributeConverter<BookingStatus, String> {

    @Override
    public String convertToDatabaseColumn(BookingStatus status) {
        if (status == null) return null;
        return status.getValue(); // always lowercase
    }

    @Override
    public BookingStatus convertToEntityAttribute(String value) {
        if (value == null) return null;
        return BookingStatus.fromValue(value); // case-insensitive
    }
}
