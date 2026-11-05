package ru.petads;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

/**
 * API сервиса объявлений о животных.
 * GET /health, GET /animals[?type=cat], GET /animals/{id}
 */
public class App {

    private static final AnimalRepository REPOSITORY = new AnimalRepository();

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        // 0.0.0.0 — чтобы API было доступно снаружи контейнера, а не только внутри
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/health", App::health);
        server.createContext("/animals", App::animals);
        server.start();
        log("Pet Ads API started on port " + port);
    }

    private static void health(HttpExchange exchange) throws IOException {
        log(exchange.getRequestMethod() + " " + exchange.getRequestURI());
        send(exchange, 200, "{\"status\":\"UP\"}");
    }

    private static void animals(HttpExchange exchange) throws IOException {
        log(exchange.getRequestMethod() + " " + exchange.getRequestURI());
        if (!"GET".equals(exchange.getRequestMethod())) {
            send(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/animals") || path.equals("/animals/")) {
            String type = queryParam(exchange.getRequestURI().getQuery(), "type");
            send(exchange, 200, Json.array(REPOSITORY.findAll(type)));
            return;
        }
        try {
            long id = Long.parseLong(path.substring("/animals/".length()));
            REPOSITORY.findById(id).ifPresentOrElse(
                    animal -> sendQuietly(exchange, 200, animal.toJson()),
                    () -> sendQuietly(exchange, 404, "{\"error\":\"Animal not found\"}"));
        } catch (NumberFormatException e) {
            send(exchange, 400, "{\"error\":\"Invalid id\"}");
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

    private static void sendQuietly(HttpExchange exchange, int status, String body) {
        try {
            send(exchange, status, body);
        } catch (IOException e) {
            log("Failed to send response: " + e.getMessage());
        }
    }

    private static void log(String message) {
        System.out.println(LocalTime.now().withNano(0) + " " + message);
    }
}
