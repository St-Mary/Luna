ARG BUILD_HOME=/home/gradle/app
FROM gradle:7.6-jdk19-alpine AS cache

ARG BUILD_HOME
WORKDIR $BUILD_HOME
ENV GRADLE_USER_HOME /cache
COPY build.gradle.kts settings.gradle.kts ./
RUN gradle build --no-daemon --stacktrace

FROM gradle:7.6-jdk19-alpine as builder
ARG BUILD_HOME
WORKDIR $BUILD_HOME
COPY --from=cache /cache /home/gradle/.gradle
COPY . $BUILD_HOME/
RUN gradle --no-daemon clean build --stacktrace

FROM openjdk:19
ARG BUILD_HOME
WORKDIR $BUILD_HOME
COPY --from=builder $BUILD_HOME/build/libs/stmary.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]