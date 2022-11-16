# java-filmorate
Общий вид БД:

![image](https://user-images.githubusercontent.com/106442300/202135940-c9d26659-1ef3-453a-bf43-d3ac77d5959d.png)

1)Запрос на получение всех фильмов. SELECT * FROM PUBLIC.FILMS LEFT JOIN FILM_GENRES FG on FILMS.FILM_ID = FG.FILM_ID;
2)Запрос на получение всех пользователей. SELECT * FROM USERS;
3)Запрос на получение рейтинга фильма. SELECT FILM_GENRES.GENRE_ID, G2.GENRE_NAME FROM FILM_GENRES LEFT JOIN GENRES G2 on G2.GENRE_ID = FILM_GENRES.GENRE_ID WHERE FILM_GENRES.FILM_ID=?;
4)Запрос на получение лайков фильма. SELECT USER_ID FROM FILM_LIKES WHERE FILM_ID = ?;
5)Запрос на получение списка друзей. SELECT FRIEND_ID FROM USERS_FRIENDSHIP WHERE USER_ID=?;
6)Запрос на обновление пользователя. UPDATE USERS SET EMAIL=?, LOGIN=?, NAME=?, BIRTHDAY=? WHERE USER_ID=?;
7)Запрос на обновление фильма. UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATE = ?, MPA = ? WHERE FILM_ID = ?;
