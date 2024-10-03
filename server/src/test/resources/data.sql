INSERT INTO users (name, email)
VALUES ('Aleksandr', 'aleksandrov@email.com'),
       ('Sergey', 'sergeev@email.com');

INSERT INTO items (name, description, is_available, owner_id, request_id)
VALUES ('Available Item', 'Description item', false, 1, 2),
       ('Not available Item', 'Description item', true, 2, 1);

INSERT INTO bookings (start_date, end_date, item_id, booker_id, status)
VALUES ('2024-09-15T12:00:00', '2024-09-20T12:00:00', 2, 1, 'WAITING'),
       ('2024-12-15T12:00:00', '2024-12-20T12:00:00', 1, 2, 'WAITING');

INSERT INTO item_requests (description, requestor_id, created)
VALUES ('Owner ItemRequest', 1, '2024-09-15T12:00:00'),
       ('User ItemRequest', 2, '2024-09-15T12:00:00');
