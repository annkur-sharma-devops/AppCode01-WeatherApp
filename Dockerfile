FROM tomcat:9.0-jdk17

# ARG = passed at build time
ARG BUILD_VERSION
ENV BUILD_VERSION_DOCKER=${BUILD_VERSION}

# Remove default webapps (optional)
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR file into Tomcat
# Download WAR from Nexus
ADD http://localhost:8899/repository/maven-release-repo-weather-app/com/example/weather-app/BUILD_VERSION_DOCKER/weather-app-BUILD_VERSION_DOCKER.war /usr/local/tomcat/webapps/weather-app-2.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
