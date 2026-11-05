package ru.petads;

import java.util.List;
import java.util.stream.Collectors;

/** Минимальная сериализация в JSON без внешних библиотек. */
final class Json {

    private Json() {
    }

    static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    static String array(List<Animal> animals) {
        return animals.stream().map(Animal::toJson).collect(Collectors.joining(",", "[", "]"));
    }
}
