package ru.practicum.shareit.booking.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.RequestBookingDto;

import java.time.LocalDateTime;
import java.util.Objects;

public class StartAndEndValidator implements ConstraintValidator<StartAndEndValid, RequestBookingDto> {
    @Override
    public void initialize(StartAndEndValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(RequestBookingDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (Objects.isNull(start) || Objects.isNull(end)) {
            return false;
        }
        return start.isBefore(end);
    }
}
