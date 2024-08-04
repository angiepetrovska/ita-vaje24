# official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# working directory in the container
WORKDIR /app

# packaged jar file 
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar

# port 6060 
EXPOSE 6060

# Run the jar file
ENTRYPOINT ["java","-jar","app.jar"]