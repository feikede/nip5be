#
# Build stage
#
# build like: docker build . -t nip5server:latest -t nip5server:1.2
#
FROM maven:3.8-openjdk-18 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

FROM openjdk:18-jdk-slim-bullseye
RUN adduser --system --group nip5
USER nip5:nip5
COPY --from=build /home/app/target/nip5server-*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
