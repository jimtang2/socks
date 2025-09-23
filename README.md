# Socks

## Purpose

The purpose of this project is to build a microservice to:
- serve a WebSocket endpoint for frontend application
- serve data stored in a Postgres database from that WebSocket endpoint
- implement [12factor](12factor.net) application design patterns

## Project Index

### Build
- Project Root: [https://dev.lab9.studio/socks/](https://dev.lab9.studio/socks/)
- Gradle Build: [https://dev.lab9.studio/socks/build.gradle](https://dev.lab9.studio/socks/build.gradle)

### Application
- @SpringBootApplication: [https://dev.lab9.studio/socks/src/main/java/studio/lab9/socks](https://dev.lab9.studio/socks/src/main/java/studio/lab9/socks)
- Application Config: [https://dev.lab9.studio/socks/src/main/resources/application.yml](https://dev.lab9.studio/socks/src/main/resources/application.yml)
- DatabaseConfig: [https://dev.lab9.studio/socks/src/main/java/studio/lab9/socks/config/DatabaseConfig.java](https://dev.lab9.studio/socks/src/main/java/studio/lab9/socks/config/DatabaseConfig.java)
- WebSocketConfig: [https://dev.lab9.studio/socks/src/main/java/studio/lab9/socks/config/WebSocketConfig.java](https://dev.lab9.studio/socks/src/main/java/studio/lab9/socks/config/WebSocketConfig.java)

### Websocket Handlers
- Path: [https://dev.lab9.studio/socks/src/main/java/studio/lab9/socks/handler](https://dev.lab9.studio/socks/src/main/java/studio/lab9/socks/handler)

### Test
- Application Tests: [https://dev.lab9.studio/socks/src/test/java/studio/lab9/socks](https://dev.lab9.studio/socks/src/test/java/studio/lab9/socks)

### Deploy
- Dockerfile CI: [https://dev.lab9.studio/socks/Dockerfile](https://dev.lab9.studio/socks/Dockerfile)
- Github CI Workflow: [https://dev.lab9.studio/socks/.github/workflows/ci.yml](https://dev.lab9.studio/socks/.github/workflows/ci.yml)


## TODO

