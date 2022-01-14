FROM 17-jdk-buster
COPY . /app
WORKDIR /app
RUN gradlew clean shadowJar
COPY --from=build /app/build/libs/*.jar /app/lagbot.jar
ENTRYPOINT ["java", "-jar", "/app/lagbot.jar"]