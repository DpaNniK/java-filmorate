DROP TABLE IF EXISTS USERS, FRIENDSHIP_STATUS, USERS_FRIENDSHIP,
    GENRES, FILMS, MPA, FILM_GENRES, FILM_LIKES;

CREATE TABLE USERS
(
    user_id  INTEGER     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    email    TEXT(50)    NOT NULL,
    login    varchar(30) NOT NULL,
    name     varchar(30),
    birthday DATE,
    CONSTRAINT constr CHECK (birthday <= CURRENT_DATE())
);

CREATE TABLE FRIENDSHIP_STATUS
(
    status_id   INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    status_name varchar(20)
);

CREATE TABLE USERS_FRIENDSHIP
(
    user_id           INTEGER REFERENCES USERS (user_id),
    friend_id         INTEGER REFERENCES USERS (user_id),
    friendship_status INTEGER REFERENCES FRIENDSHIP_STATUS (status_id)
);

CREATE TABLE GENRES
(
    genre_id   INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    genre_name varchar(20)
);

CREATE TABLE MPA
(
    mpa_id   INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    mpa_name varchar(20)
);

CREATE TABLE FILMS
(
    film_id      INTEGER     NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(80) NOT NULL,
    description  VARCHAR(200),
    release_date DATE,
    duration     INTEGER     NOT NULL,
    rate         INTEGER,
    mpa          INTEGER REFERENCES MPA (mpa_id) ON DELETE CASCADE,
    CONSTRAINT con CHECK (release_date <= CURRENT_DATE() AND duration > 0)
);


CREATE TABLE FILM_GENRES
(
    film_id  INTEGER REFERENCES FILMS (film_id),
    genre_id INTEGER REFERENCES GENRES (genre_id)
);

CREATE TABLE FILM_LIKES
(
    film_id INTEGER REFERENCES FILMS (film_id),
    user_id INTEGER REFERENCES USERS (user_id)
);