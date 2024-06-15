# Using multi-stage build to reduce the size of the final image
# Stage 1: Build the application
FROM gradle:8.8.0-jdk17 AS build
USER root
COPY --chown=gradle:gradle . /home/gradle/project
WORKDIR /home/gradle/project
RUN gradle build -x test --no-daemon

# Stage 2: Run the application
FROM openjdk:17-slim
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
