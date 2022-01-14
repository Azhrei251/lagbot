FROM openjdk:17-jdk-buster
COPY . /app
WORKDIR /app
RUN ./gradlew --no-daemon shadowJar
RUN cp /app/build/libs/*.jar /app/lagbot.jar
ENTRYPOINT ["java", "-jar", "/app/lagbot.jar"]
