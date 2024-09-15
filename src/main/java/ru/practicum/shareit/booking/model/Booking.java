package ru.practicum.shareit.booking.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "bookings")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "start_date")
    LocalDateTime start;
    @Column(name = "end_date")
    LocalDateTime end;
    @OneToOne
    @JoinColumn(name = "item_id")
    Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id")
    User booker;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    BookingStatus status;
}
