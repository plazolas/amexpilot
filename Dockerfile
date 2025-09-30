# Alpine Linux with OpenJDK 17
FROM openjdk:17-jdk-alpine

# Copy jar file
COPY ./target/amexpilot-1.0.0-SNAPSHOT-fat.jar /amexpilot-1.0.0-SNAPSHOT-fat.jar

EXPOSE 8888

# run the app
CMD ["java", "-jar", "/amexpilot-1.0.0-SNAPSHOT-fat.jar"]

