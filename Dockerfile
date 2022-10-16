FROM amazoncorretto:17-alpine3.16
COPY . /app
WORKDIR /app
RUN ./gradlew --no-daemon shadowJar

FROM amazoncorretto:17-alpine3.16
WORKDIR /app
COPY --from=0 /app/build/libs/*.jar /app/lagbot.jar
ENTRYPOINT ["java", "-jar", "/app/lagbot.jar"]
