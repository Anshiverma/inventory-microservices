# Use JDK base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy all files
COPY . .

# Compile Java files
RUN javac *.java

# Expose port
EXPOSE 8082

# Run the service
CMD ["java", "OrderService"]
