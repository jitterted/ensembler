FROM eclipse-temurin:23-jdk as build
COPY . /app
WORKDIR /app
RUN ./mvnw --no-transfer-progress clean package -DskipTests
RUN mv -f target/*.jar app.jar

FROM eclipse-temurin:23-jre
ARG PORT
ENV PORT=${PORT}
COPY --from=build /app/app.jar .
RUN useradd runtime
USER runtime
ENTRYPOINT [ "java", "-Dserver.port=${PORT}", "-jar", "app.jar" ]