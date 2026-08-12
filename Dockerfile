# ---- Etapa 1: build ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copia primeiro o wrapper e o pom para aproveitar cache de dependências
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

# Copia o restante do código e builda o jar (sem rodar os testes)
COPY src ./src
RUN ./mvnw -B clean package -DskipTests

# ---- Etapa 2: runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# O Render injeta a variável PORT automaticamente; o app já está configurado
# para escutar em ${PORT:8080} (veja application.properties)
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
