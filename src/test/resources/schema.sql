-- Create the MemberEntity table
CREATE TABLE member_entity
(
    email VARCHAR(255) PRIMARY KEY
);

-- Insert dummy data into MemberEntity
INSERT INTO member_entity (email)
VALUES ('user1@example.com');
INSERT INTO member_entity (email)
VALUES ('user2@example.com');
INSERT INTO member_entity (email)
VALUES ('user3@example.com');

-- Create the PlaceEntity table
CREATE TABLE place_entity
(
    id      BIGSERIAL PRIMARY KEY,
    country VARCHAR(255),
    city    VARCHAR(255),
    name    VARCHAR(255)
);

-- Create table for place images
CREATE TABLE place_images
(
    place_id  BIGINT,
    image_url VARCHAR(255),
    FOREIGN KEY (place_id) REFERENCES place_entity (id)
);

-- Insert dummy data into PlaceEntity
INSERT INTO place_entity (country, city, name)
VALUES ('USA', 'New York', 'Central Park');
INSERT INTO place_entity (country, city, name)
VALUES ('France', 'Paris', 'Eiffel Tower');
INSERT INTO place_entity (country, city, name)
VALUES ('Japan', 'Tokyo', 'Shinjuku Gyoen');

-- Create the TripEntity table
CREATE TABLE trip_entity
(
    id          BIGSERIAL PRIMARY KEY,
    userId      VARCHAR(255),
    name        VARCHAR(255),
    description TEXT,
    startDate   TIMESTAMP,
    endDate     TIMESTAMP
);

-- Insert dummy data into TripEntity
INSERT INTO trip_entity (userId, name, description, startDate, endDate)
VALUES ('1', 'Summer Vacation', 'Trip to the beach', '2023-06-15 00:00:00', '2023-06-20 00:00:00');
INSERT INTO trip_entity (userId, name, description, startDate, endDate)
VALUES ('2', 'Winter Skiing', 'Ski trip in the mountains', '2023-12-05 00:00:00', '2023-12-10 00:00:00');
INSERT INTO trip_entity (userId, name, description, startDate, endDate)
VALUES ('3', 'Spring Break', 'Enjoying the spring flowers', '2024-04-05 00:00:00', '2024-04-10 00:00:00');

-- Create the NotificationOutbox table
CREATE TABLE notification_outbox
(
    notification_id BIGSERIAL PRIMARY KEY,
    tripEntity_id  BIGINT,
    status         VARCHAR(255),
    FOREIGN KEY (tripEntity_id) REFERENCES trip_entity (id)
);

-- -- Insert dummy data into NotificationOutbox
-- INSERT INTO notification_outbox (tripEntity_id, status)
-- VALUES (100, 'SENT');
-- INSERT INTO notification_outbox (tripEntity_id, status)
-- VALUES (200, 'PENDING');
-- INSERT INTO notification_outbox (tripEntity_id, status)
-- VALUES (300, 'FAILED');

-- Create join tables for ManyToMany relationships
CREATE TABLE trip_members
(
    trip_id      BIGINT,
    member_email VARCHAR(255),
    PRIMARY KEY (trip_id, member_email),
    FOREIGN KEY (trip_id) REFERENCES trip_entity (id),
    FOREIGN KEY (member_email) REFERENCES member_entity (email)
);

CREATE TABLE trip_place
(
    trip_id  BIGINT,
    place_id BIGINT,
    PRIMARY KEY (trip_id, place_id),
    FOREIGN KEY (trip_id) REFERENCES trip_entity (id),
    FOREIGN KEY (place_id) REFERENCES place_entity (id)
);
