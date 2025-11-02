FROM openjdk:11
WORKDIR /app
COPY . /app
RUN javac *.java
EXPOSE 8081
CMD ["java", "ProductService"]
