# java-filmorate
- сервис c рекомендационной системой, позволяющий выбрать пользователю сериал или фильм для просмотра.

Программа может принимать, обновлять и возвращать пользователей и фильмы.

Есть возможность добавлять оценки и комментарии пользователей фильмам, добавлять пользователей в друзья (с возможностью последующего удаления), добавлять лайки комментариям, выводить топ-n фильмов по рейтингу, выводить список рекомендуемых общих с другом фильмов с сортировкой по популярности. В приложении реализована рекомендательная система поиска фильмов по пользователям с максимальным пересечением лайков. Для фильмов реализована возможность добавлять режиссёров с последующим поиском фильмов по режиссёру.

Приложение не имеет интерфейса, взаимодействие с ним происходит при помощи REST API. Посмотреть все эндпоинты можно в классах:
+ FilmController.java
+ UserController.java

Приложение построено на фреймворке Spring Boot, общение с БД происходит с помощью Spring JDBC.

Проект реализован на версии Java 11

Дальнейшая разработка проекта представлена в репозитории: https://github.com/LenaChaldina/java-filmorate
