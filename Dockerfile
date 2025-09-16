FROM tomcat:9.0-jdk17

# ARG = passed at build time
ARG BUILD_VERSION

# Remove default webapps (optional)
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy WAR file from build context into Tomcat
COPY weather-app-${BUILD_VERSION}.war /usr/local/tomcat/webapps/weather-app.war

# Tomcat default port
EXPOSE 8080

CMD ["catalina.sh", "run"]
