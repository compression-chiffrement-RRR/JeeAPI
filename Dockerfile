FROM openjdk:11.0-jre-slim
RUN groupadd spring && useradd springuser -g spring
USER springuser:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
