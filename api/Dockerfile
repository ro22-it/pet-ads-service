# Этап 1: компиляция исходников (нужен полный JDK)
FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
# JDBC-драйвер PostgreSQL с Maven Central, контрольная сумма защищает от подмены файла
ADD --checksum=sha256:73914527305a40cce504b0d3d90b23caf565136912d607ae8ae7c5895512332c \
    https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.14/postgresql-42.7.14.jar \
    lib/postgresql.jar
COPY src ./src
RUN mkdir out && javac -encoding UTF-8 -cp lib/postgresql.jar -d out $(find src -name "*.java")

# Этап 2: запуск (достаточно JRE — образ меньше)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /src/out ./classes
COPY --from=build /src/lib ./lib
ENV PORT=8080
EXPOSE 8080
CMD ["java", "-cp", "classes:lib/postgresql.jar", "ru.petads.App"]
