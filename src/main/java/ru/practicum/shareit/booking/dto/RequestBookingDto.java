package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestBookingDto {
    @FutureOrPresent
    @DateTimeFormat(pattern = "yyyy:MM:ddThh:MM:ss")
    LocalDateTime start;
    @Future
    @DateTimeFormat(pattern = "yyyy:MM:ddThh:MM:ss")
    LocalDateTime end;
    Long itemId;
}
