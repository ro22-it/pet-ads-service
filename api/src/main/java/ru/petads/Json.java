package ru.petads;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Минимальная работа с JSON без внешних библиотек. */
final class Json {

    private Json() {
    }

    static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    static String array(List<Animal> animals) {
        return animals.stream().map(Animal::toJson).collect(Collectors.joining(",", "[", "]"));
    }

    /**
     * Разбирает плоский JSON-объект вида {"name":"Мурка","age":2}.
     * Значения возвращаются строками; вложенные объекты и массивы не поддерживаются.
     */
    static Map<String, String> parseObject(String json) {
        return new Parser(json).object();
    }

    private static final class Parser {
        private final String s;
        private int pos;

        Parser(String s) {
            this.s = s == null ? "" : s;
        }

        Map<String, String> object() {
            Map<String, String> result = new LinkedHashMap<>();
            expect('{');
            skipSpaces();
            if (peek() == '}') {
                pos++;
                return result;
            }
            while (true) {
                skipSpaces();
                String key = string();
                expect(':');
                skipSpaces();
                result.put(key, value());
                skipSpaces();
                char c = next();
                if (c == '}') {
                    break;
                }
                if (c != ',') {
                    throw error("ожидалась ',' или '}'");
                }
            }
            skipSpaces();
            if (pos != s.length()) {
                throw error("лишние символы после объекта");
            }
            return result;
        }

        private String value() {
            if (peek() == '"') {
                return string();
            }
            int start = pos;
            while (pos < s.length() && ",} \t\r\n".indexOf(s.charAt(pos)) < 0) {
                pos++;
            }
            String raw = s.substring(start, pos);
            if (raw.isEmpty() || raw.startsWith("{") || raw.startsWith("[")) {
                throw error("неподдерживаемое значение");
            }
            return raw.equals("null") ? null : raw;
        }

        private String string() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = next();
                if (c == '"') {
                    return sb.toString();
                }
                if (c != '\\') {
                    sb.append(c);
                    continue;
                }
                char e = next();
                switch (e) {
                    case 'n' -> sb.append('\n');
                    case 't' -> sb.append('\t');
                    case 'r' -> sb.append('\r');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'u' -> {
                        if (pos + 4 > s.length()) {
                            throw error("обрезанный \\u");
                        }
                        sb.append((char) Integer.parseInt(s.substring(pos, pos + 4), 16));
                        pos += 4;
                    }
                    default -> sb.append(e);
                }
            }
        }

        private void expect(char c) {
            skipSpaces();
            if (next() != c) {
                throw error("ожидался символ '" + c + "'");
            }
        }

        private void skipSpaces() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
                pos++;
            }
        }

        private char peek() {
            if (pos >= s.length()) {
                throw error("неожиданный конец");
            }
            return s.charAt(pos);
        }

        private char next() {
            char c = peek();
            pos++;
            return c;
        }

        private IllegalArgumentException error(String message) {
            return new IllegalArgumentException("Некорректный JSON: " + message + " (позиция " + pos + ")");
        }
    }
}
