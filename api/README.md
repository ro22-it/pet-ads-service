# Pet Ads API (КП «Контейнер»)

Простое API сервиса объявлений о животных на Java 21 без внешних библиотек
(встроенный `com.sun.net.httpserver`). Данные пока хранятся в памяти.

## Endpoints
| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/health` | проверка, что сервис жив |
| GET | `/animals` | список объявлений |
| GET | `/animals?type=cat` | объявления одного вида |
| GET | `/animals/{id}` | одно объявление (404, если нет) |

## Запуск без Docker
```bash
cd api
mkdir -p out && javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out ru.petads.App
```

## Сборка и запуск в Docker
```bash
cd api
docker build -t pet-ads-api .
docker run -d --name pet-ads-api -p 8080:8080 pet-ads-api
```

## Проверка
```bash
docker ps                                   # контейнер pet-ads-api в статусе Up
curl http://localhost:8080/health           # {"status":"UP"}
curl http://localhost:8080/animals          # список объявлений
curl http://localhost:8080/animals/2        # одно объявление
docker logs pet-ads-api                     # строка запуска и все запросы
```

## Dockerfile
- `FROM eclipse-temurin:21-jdk AS build` — этап сборки с полным JDK;
- `RUN javac ...` — компиляция исходников внутри контейнера;
- `FROM eclipse-temurin:21-jre` — финальный образ только с JRE, он меньше;
- `COPY --from=build` — переносим скомпилированные классы из этапа сборки;
- `EXPOSE 8080` — порт приложения;
- `CMD ["java", ...]` — команда запуска API.

Флаг `-p 8080:8080` пробрасывает порт контейнера на компьютер, поэтому API доступно
снаружи. Сервер слушает `0.0.0.0`, иначе запросы извне контейнера не дошли бы.

## Результаты проверки
Скриншоты и выводы команд лежат в [../docs/kp3](../docs/kp3).
