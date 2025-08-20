# Dockerfile
FROM openjdk:21-jdk-slim

LABEL maintainer="timatix@example.com"
LABEL description="Timatix Booking Services Backend"

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:8081/api/health || exit 1

# Run the application
CMD ["java", "-jar", "target/servicebooking-0.0.1-SNAPSHOT.jar"]