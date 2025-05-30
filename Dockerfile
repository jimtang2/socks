FROM ubuntu:24.04 AS builder

WORKDIR /app

RUN apt-get update && apt-get install -y curl unzip make openjdk-21-jdk

ENV PATH="/usr/local/gradle/bin:${PATH}"

COPY . .

RUN make setup && make build

FROM amazoncorretto:21-alpine-jdk

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar .

ENV SERVER_PORT=8000

EXPOSE 8000

CMD ["java", "-jar", "socks.jar"]