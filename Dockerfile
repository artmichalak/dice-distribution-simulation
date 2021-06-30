FROM gradle:6.7.0-jre15 AS build
COPY --chown=gradle:gradle . /app

WORKDIR /app
RUN gradle build --no-daemon --console verbose
RUN ls -la /app/build/libs

FROM openjdk:15.0.1-jdk-oraclelinux7

COPY --from=build /app/build/libs/*.jar /app/dice-distribution-simulation.jar

EXPOSE 8080

ENTRYPOINT ["java","-XX:+UseG1GC","-XX:MaxRAMPercentage=80","-jar","/app/dice-distribution-simulation.jar"]
