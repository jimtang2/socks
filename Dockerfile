FROM socks-builder AS builder

RUN apt-get update && apt-get install -y libudev-dev  # Use apt-get for Ubuntu builder

WORKDIR /app
COPY . .
RUN make setup && make build

FROM amazoncorretto:21-alpine-jdk

RUN apk update && apk add --no-cache eudev-libs  # Use apk for Alpine runtime

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar .
ENV SERVER_PORT=8000
EXPOSE 8000
CMD ["java", "-jar", "*.jar"]  # Wildcard for JAR