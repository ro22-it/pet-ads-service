# Pet Ads Service — сервис объявлений о животных

Учебный проект по дисциплине «Введение в DevSecOps».

Сервис хранит объявления о животных, которых отдают даром или продают.
Пользователь может посмотреть список объявлений, добавить своё и снять его,
когда животное нашло дом.

Статус: готово API в Docker-контейнере (КП 3), следующий шаг — PostgreSQL и Docker Compose.

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
