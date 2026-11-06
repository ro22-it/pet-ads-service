# Pet Ads Service — сервис объявлений о животных

Учебный проект по дисциплине «Введение в DevSecOps».

Сервис хранит объявления о животных, которых отдают даром или продают.
Пользователь может посмотреть список объявлений, добавить своё и снять его,
когда животное нашло дом.

Статус: CRUD API с PostgreSQL запускается через Docker Compose (КП 4), следующий шаг — архитектура (C4).

## Запуск (КП 4 — Compose)
```bash
docker compose up -d --build
docker compose ps
curl http://localhost:8080/animals
```

Состав `docker-compose.yml`:
| Сервис | Образ | Что делает |
|--------|-------|------------|
| `db` | `postgres:16` | база `app`, пользователь `postgres` / `1234`, данные в томе `db_data` → `/var/lib/postgresql/data` |
| `api` | сборка из `./api` | CRUD API на порту 8080, подключается к `db` по имени сервиса |

- `environment` — параметры базы (`POSTGRES_*`) и подключения API (`DB_URL`, `DB_USER`, `DB_PASSWORD`);
- `depends_on` с `condition: service_healthy` — API стартует только после того, как `pg_isready` подтвердил готовность базы;
- том `db_data` хранит данные вне контейнера, поэтому они переживают `docker compose down` → `up`
  (удаляются только при `docker compose down -v`).

### Примеры запросов
```bash
# список и фильтр
curl http://localhost:8080/animals
curl "http://localhost:8080/animals?type=cat"

# создать
curl -X POST http://localhost:8080/animals -H "Content-Type: application/json" \
  -d '{"name":"Мурка","type":"cat","breed":"Сиамская","age":3,"price":0}'

# получить, изменить, удалить
curl http://localhost:8080/animals/5
curl -X PUT http://localhost:8080/animals/5 -H "Content-Type: application/json" \
  -d '{"name":"Мурка","type":"cat","breed":"Сиамская","age":3,"price":1500}'
curl -X DELETE http://localhost:8080/animals/5

# логи и остановка
docker compose logs api
docker compose down
```

Результаты проверки (скриншоты и выводы `compose ps`, `curl`, `compose logs`, проверка сохранности данных) —
в [docs/kp4](docs/kp4).

## Работа с Git (КП 1)
- ветки: `main`, `feature/crud-api`, `feature/filters`, `docs/readme-status`;
- слияния `feature/crud-api` и `feature/filters` в `main` без конфликтов;
- слияние `docs/readme-status` вызвало конфликт — описание в [git-conflict.md](git-conflict.md);
- граф веток в GitHub Network — [git-network.png](git-network.png)
  (онлайн: https://github.com/ro22-it/pet-ads-service/network),
  он же в терминале — [git-log-graph.png](git-log-graph.png).

## Структура репозитория
| Путь | Контрольная точка |
|------|-------------------|
| `README.md`, `project-notes.md`, `api-plan.md`, `git-conflict.md`, `git-network.png` | КП 1 — Git |
| `linux/` | КП 2 — Linux (отчёт и команды) |
| `api/` | КП 3 — API в Docker-контейнере |
| `docker-compose.yml`, `docs/kp4/` | КП 4 — Compose: CRUD API + PostgreSQL |
