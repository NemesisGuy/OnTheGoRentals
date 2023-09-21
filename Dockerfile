FROM adoptopenjdk:20-jre-hotspot
WORKDIR /app

# Clone the repository
RUN git clone https://github.com/NemesisGuy/OnTheGoRentals.git .

# Build the Spring Boot application
RUN ./mvnw package

# Specify the path to the JAR file after building
CMD ["java", "-jar", "target/OnTheGoRentals.jar"]

LABEL authors="Peter Buckingham"

