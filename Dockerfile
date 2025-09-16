FROM socks-builder AS builder

RUN apt-get update && apt-get install -y libudev-dev

WORKDIR /app
COPY . .
RUN make setup && make build

FROM amazoncorretto:21-alpine-jdk

RUN apk update && apk add --no-cache eudev-libs

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar .
ENV SERVER_PORT=8000
EXPOSE 8000
CMD ["java", "-jar", "socks.jar"] 