package ru.petads;

import java.util.List;
import java.util.Optional;

/** Хранилище объявлений в памяти. В КП Compose будет заменено на PostgreSQL. */
public class AnimalRepository {

    private final List<Animal> animals = List.of(
            new Animal(1, "Барсик", "cat", "Британская", 1, 0),
            new Animal(2, "Рекс", "dog", "Бульдог", 4, 10000),
            new Animal(3, "Кеша", "bird", "Волнистый попугай", 1, 3000),
            new Animal(4, "Граф", "cat", "Мейн-кун", 2, 0));

    public List<Animal> findAll(String type) {
        if (type == null || type.isBlank()) {
            return animals;
        }
        return animals.stream().filter(a -> a.type().equalsIgnoreCase(type)).toList();
    }

    public Optional<Animal> findById(long id) {
        return animals.stream().filter(a -> a.id() == id).findFirst();
    }
}
