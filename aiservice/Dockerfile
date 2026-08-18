# Use a current and official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk-jammy

# Set the working directory in the container
WORKDIR /app

# Copy the executable JAR file from the target directory to the container
COPY target/aiservice-0.0.1-SNAPSHOT.jar app.jar

# Make port 8084 available to the world outside this container
EXPOSE 8084

# Run the JAR file when the container launches
ENTRYPOINT ["java", "-jar", "app.jar"]
