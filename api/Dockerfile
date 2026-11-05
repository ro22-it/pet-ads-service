# Этап 1: компиляция исходников (нужен полный JDK)
FROM eclipse-temurin:21-jdk AS build
WORKDIR /src
COPY src ./src
RUN mkdir out && javac -encoding UTF-8 -d out $(find src -name "*.java")

# Этап 2: запуск (достаточно JRE — образ меньше)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /src/out ./classes
ENV PORT=8080
EXPOSE 8080
CMD ["java", "-cp", "classes", "ru.petads.App"]
