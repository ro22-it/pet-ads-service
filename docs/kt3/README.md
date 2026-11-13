# КТ «Контейнер» — результаты проверки

Все выводы получены реальным запуском (Docker 29 через Colima, macOS).

| Шаг | Скриншот | Полный вывод |
|-----|----------|--------------|
| Сборка образа `docker build` | [01-docker-build.png](01-docker-build.png) | [01-docker-build.txt](01-docker-build.txt) |
| Запуск `docker run -p 8080:8080` | [02-docker-run.png](02-docker-run.png) | [02-docker-run.txt](02-docker-run.txt) |
| Контейнер в `docker ps` | [03-docker-ps.png](03-docker-ps.png) | [03-docker-ps.txt](03-docker-ps.txt) |
| Запросы к API через `curl` | [04-curl.png](04-curl.png) | [04-curl.txt](04-curl.txt) |
| Логи `docker logs` | [05-docker-logs.png](05-docker-logs.png) | [05-docker-logs.txt](05-docker-logs.txt) |
| Ответ API в браузере | [06-browser-animals.png](06-browser-animals.png) | — |

Что видно:
- образ собирается в два этапа: компиляция на JDK, запуск на JRE;
- контейнер `pet-ads-api` в статусе `Up`, порт `0.0.0.0:8080->8080/tcp`;
- `/health` → `200 {"status":"UP"}`, `/animals` → список, `/animals/2` → одно объявление, `/animals/99` → `404`;
- в `docker logs` строка запуска и каждый запрос.
