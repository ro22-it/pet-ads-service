# КП «Compose» — результаты проверки

Все выводы получены реальным запуском (Docker 29 + Docker Compose через Colima, macOS).

| Шаг | Скриншот | Полный вывод |
|-----|----------|--------------|
| Сервисы в `docker compose ps` | [01-compose-ps.png](01-compose-ps.png) | [01-compose-ps.txt](01-compose-ps.txt) |
| CRUD-запросы через `curl` | [02-curl-crud.png](02-curl-crud.png) | [02-curl-crud.txt](02-curl-crud.txt) |
| Логи `docker compose logs` | [03-compose-logs.png](03-compose-logs.png) | [03-compose-logs.txt](03-compose-logs.txt) |
| Данные после `down` → `up` | [04-persistence.png](04-persistence.png) | [04-persistence.txt](04-persistence.txt) |

Что видно:
- оба контейнера `pet-ads-api` и `pet-ads-db` в статусе `Up`, база — `healthy`;
- API стартует только после того, как PostgreSQL прошёл проверку готовности (`Container pet-ads-db Healthy` → `pet-ads-api Started`);
- работают все операции CRUD: `POST` создаёт объявление `id=5`, `PUT` меняет цену, `DELETE` удаляет `id=2`, после чего `GET /animals/2` → `404`;
- запрос без обязательного поля `type` → `400 Bad Request`;
- в логах API — подключение к PostgreSQL, строка запуска и каждый запрос;
- после `docker compose down` том `db_data` остался, а после `up` объявление «Мурка» с ценой 1500 на месте, удалённый «Рекс» не вернулся — данные хранятся в томе, а не в контейнере.
