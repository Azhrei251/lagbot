FROM amazoncorretto:17-alpine3.16
COPY . /app
WORKDIR /app
RUN ./gradlew --no-daemon shadowJar
RUN cp /app/build/libs/*.jar /app/lagbot.jar
RUN rm -f !lagbot.jar
ENTRYPOINT ["java", "-jar", "/app/lagbot.jar"]
