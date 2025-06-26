# Use a base image with Java installed
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/OnTheGoRentals.jar /app/OnTheGoRentals.jar

# Expose the port the application will run on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/OnTheGoRentals.jar"]
