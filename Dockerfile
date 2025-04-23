# Stage 1: Build
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /app
COPY . .
# Skip tests for faster build and skip git plugin issues
RUN mvn clean package -DskipTests -Dgit.skip=true

# Stage 2: Runtime
FROM tomcat:9.0-jdk17-openjdk-slim

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the WAR file to Tomcat webapps directory
COPY --from=build /app/target/adminapi-v3.0.0.war /usr/local/tomcat/webapps/ROOT.war

# Tomcat port
EXPOSE 8082

CMD ["catalina.sh", "run"]