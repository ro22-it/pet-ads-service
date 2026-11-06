package ru.petads;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Хранилище объявлений в PostgreSQL (таблица animals). */
public class AnimalRepository {

    private static final String COLUMNS = "id, name, type, breed, age, price";

    private final String url;
    private final String user;
    private final String password;

    public AnimalRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /** Создаёт таблицу и стартовые объявления, если таблица пустая. */
    public void init() throws SQLException {
        try (Connection c = connect(); Statement st = c.createStatement()) {
            st.execute("""
                    CREATE TABLE IF NOT EXISTS animals (
                        id    BIGSERIAL PRIMARY KEY,
                        name  TEXT    NOT NULL,
                        type  TEXT    NOT NULL,
                        breed TEXT    NOT NULL DEFAULT '',
                        age   INTEGER NOT NULL DEFAULT 0,
                        price INTEGER NOT NULL DEFAULT 0
                    )""");
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM animals")) {
                rs.next();
                if (rs.getLong(1) > 0) {
                    return;
                }
            }
        }
        create(new Animal(0, "Барсик", "cat", "Британская", 1, 0));
        create(new Animal(0, "Рекс", "dog", "Бульдог", 4, 10000));
        create(new Animal(0, "Кеша", "bird", "Волнистый попугай", 1, 3000));
        create(new Animal(0, "Граф", "cat", "Мейн-кун", 2, 0));
    }

    public List<Animal> findAll(String type) throws SQLException {
        boolean filter = type != null && !type.isBlank();
        String sql = "SELECT " + COLUMNS + " FROM animals"
                + (filter ? " WHERE LOWER(type) = LOWER(?)" : "") + " ORDER BY id";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            if (filter) {
                ps.setString(1, type);
            }
            try (ResultSet rs = ps.executeQuery()) {
                List<Animal> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(map(rs));
                }
                return result;
            }
        }
    }

    public Optional<Animal> findById(long id) throws SQLException {
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNS + " FROM animals WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public Animal create(Animal a) throws SQLException {
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO animals (name, type, breed, age, price) VALUES (?, ?, ?, ?, ?) RETURNING "
                             + COLUMNS)) {
            fill(ps, a);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return map(rs);
            }
        }
    }

    public Optional<Animal> update(long id, Animal a) throws SQLException {
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement(
                     "UPDATE animals SET name = ?, type = ?, breed = ?, age = ?, price = ? WHERE id = ? RETURNING "
                             + COLUMNS)) {
            fill(ps, a);
            ps.setLong(6, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public boolean delete(long id) throws SQLException {
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM animals WHERE id = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    private static void fill(PreparedStatement ps, Animal a) throws SQLException {
        ps.setString(1, a.name());
        ps.setString(2, a.type());
        ps.setString(3, a.breed());
        ps.setInt(4, a.age());
        ps.setInt(5, a.price());
    }

    private static Animal map(ResultSet rs) throws SQLException {
        return new Animal(rs.getLong("id"), rs.getString("name"), rs.getString("type"),
                rs.getString("breed"), rs.getInt("age"), rs.getInt("price"));
    }
}
