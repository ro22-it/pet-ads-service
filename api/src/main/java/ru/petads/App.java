package ru.petads;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;

/**
 * API сервиса объявлений о животных.
 * GET /health
 * GET /animals[?type=cat], GET /animals/{id}
 * POST /animals, PUT /animals/{id}, DELETE /animals/{id}
 */
public class App {

    private static AnimalRepository repository;

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(env("PORT", "8080"));
        repository = new AnimalRepository(
                env("DB_URL", "jdbc:postgresql://localhost:5432/app"),
                env("DB_USER", "postgres"),
                env("DB_PASSWORD", "1234"));
        initDatabase();

        // 0.0.0.0 — чтобы API было доступно снаружи контейнера, а не только внутри
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/health", App::health);
        server.createContext("/animals", App::animals);
        server.start();
        log("Pet Ads API started on port " + port);
    }

    /** База может подниматься дольше API, поэтому пробуем подключиться несколько раз. */
    private static void initDatabase() throws InterruptedException, SQLException {
        for (int attempt = 1; ; attempt++) {
            try {
                repository.init();
                log("Connected to PostgreSQL");
                return;
            } catch (SQLException e) {
                if (attempt == 10) {
                    throw e;
                }
                log("PostgreSQL is not ready (" + e.getMessage() + "), retry " + attempt + "/10");
                Thread.sleep(2000);
            }
        }
    }

    private static void health(HttpExchange exchange) throws IOException {
        log(exchange.getRequestMethod() + " " + exchange.getRequestURI());
        send(exchange, 200, "{\"status\":\"UP\"}");
    }

    private static void animals(HttpExchange exchange) throws IOException {
        log(exchange.getRequestMethod() + " " + exchange.getRequestURI());
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        boolean collection = path.equals("/animals") || path.equals("/animals/");
        try {
            if (collection) {
                switch (method) {
                    case "GET" -> {
                        String type = queryParam(exchange.getRequestURI().getQuery(), "type");
                        send(exchange, 200, Json.array(repository.findAll(type)));
                    }
                    case "POST" -> send(exchange, 201, repository.create(readAnimal(exchange)).toJson());
                    default -> send(exchange, 405, "{\"error\":\"Method not allowed\"}");
                }
                return;
            }
            long id = Long.parseLong(path.substring("/animals/".length()));
            switch (method) {
                case "GET" -> sendFound(exchange, repository.findById(id));
                case "PUT" -> sendFound(exchange, repository.update(id, readAnimal(exchange)));
                case "DELETE" -> {
                    if (repository.delete(id)) {
                        send(exchange, 200, "{\"deleted\":" + id + "}");
                    } else {
                        send(exchange, 404, "{\"error\":\"Animal not found\"}");
                    }
                }
                default -> send(exchange, 405, "{\"error\":\"Method not allowed\"}");
            }
        } catch (NumberFormatException e) {
            send(exchange, 400, "{\"error\":\"Invalid id\"}");
        } catch (IllegalArgumentException e) {
            send(exchange, 400, "{\"error\":\"" + Json.escape(e.getMessage()) + "\"}");
        } catch (SQLException e) {
            log("Database error: " + e.getMessage());
            send(exchange, 500, "{\"error\":\"Database error\"}");
        }
    }

    /** Читает объявление из тела запроса. name и type обязательны. */
    private static Animal readAnimal(HttpExchange exchange) throws IOException {
        String body;
        try (InputStream in = exchange.getRequestBody()) {
            body = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        Map<String, String> fields = Json.parseObject(body);
        String name = fields.get("name");
        String type = fields.get("type");
        if (name == null || name.isBlank() || type == null || type.isBlank()) {
            throw new IllegalArgumentException("Fields name and type are required");
        }
        try {
            return new Animal(0, name, type,
                    fields.getOrDefault("breed", ""),
                    Integer.parseInt(fields.getOrDefault("age", "0")),
                    Integer.parseInt(fields.getOrDefault("price", "0")));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Fields age and price must be integers");
        }
    }

    private static void sendFound(HttpExchange exchange, Optional<Animal> animal) throws IOException {
        if (animal.isPresent()) {
            send(exchange, 200, animal.get().toJson());
        } else {
            send(exchange, 404, "{\"error\":\"Animal not found\"}");
        }
    }

    private static String queryParam(String query, String name) {
        if (query == null) {
            return null;
        }
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(name)) {
                return kv[1];
            }
        }
        return null;
    }

    private static void send(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static String env(String name, String defaultValue) {
        return System.getenv().getOrDefault(name, defaultValue);
    }

    private static void log(String message) {
        System.out.println(LocalTime.now().withNano(0) + " " + message);
    }
}
