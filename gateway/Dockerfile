FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar shareit-geteway.jar
ENTRYPOINT ["java", "-jar", "shareit-geteway.jar"]
RUN apk add --no-cache tzdata
ENV TZ 	Europe/Moscow