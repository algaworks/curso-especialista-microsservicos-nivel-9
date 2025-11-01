FROM eclipse-temurin:21-jre-alpine-3.21

ENV JAR_NAME=delivery-tracking.jar \
    SERVER_PORT=8080 \
    TZ=America/Sao_Paulo

RUN addgroup -S spring && adduser -S spring -G spring && \
    apk update && \
    apk add --no-cache wget tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app

COPY build/libs/$JAR_NAME .

COPY --chmod=777 docker/docker-entrypoint.sh .

USER spring

EXPOSE 8080

ENTRYPOINT ["./docker-entrypoint.sh"]