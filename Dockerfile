FROM openjdk:10-jre
EXPOSE 8080
ADD target/scala-2.12/pacman-multiplayer-fp-assembly-1.0.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
