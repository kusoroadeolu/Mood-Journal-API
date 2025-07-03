FROM eclipse-temurin:23-jdk

#LABEL maintainer="eastw"

WORKDIR /app

COPY target/moodjournal-0.0.1-SNAPSHOT.jar app.jar
COPY wait-for-it.sh wait-for-it.sh

RUN chmod +x wait-for-it.sh

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]




