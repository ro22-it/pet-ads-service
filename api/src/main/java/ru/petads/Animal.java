package ru.petads;

/** Объявление о животном. price = 0 — отдаю даром. */
public record Animal(long id, String name, String type, String breed, int age, int price) {

    public String toJson() {
        return String.format(
                "{\"id\":%d,\"name\":\"%s\",\"type\":\"%s\",\"breed\":\"%s\",\"age\":%d,\"price\":%d}",
                id, Json.escape(name), Json.escape(type), Json.escape(breed), age, price);
    }
}
