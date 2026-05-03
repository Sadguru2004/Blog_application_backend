# Use official Java runtime
FROM openjdk:17

# Set working directory
WORKDIR /app

# Copy your project jar (we will build it in render)
COPY target/blogapplication-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]