FROM socks-builder AS builder

RUN apk update && apk add --no-cache libudev

WORKDIR /app

COPY . .

RUN make setup && make build

FROM amazoncorretto:21-alpine-jdk

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar .

ENV SERVER_PORT=8000

EXPOSE 8000

CMD ["java", "-jar", "socks.jar"]