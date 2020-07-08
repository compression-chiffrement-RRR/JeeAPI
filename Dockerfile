FROM openjdk:11.0-jre-slim
RUN groupadd spring && useradd springuser -g spring
USER springuser:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
USER root
RUN chmod 777 ./
USER springuser:spring
ENTRYPOINT ["java","-jar","/app.jar"]
