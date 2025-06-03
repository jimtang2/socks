FROM ubuntu:24.04

RUN apt-get update && apt-get install -y git curl unzip make openjdk-21-jdk

