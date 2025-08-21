# 🌦️ Weather App

A simple **Java Servlet-based Weather Application** packaged as a traditional **WAR file** for deployment on **Apache Tomcat**.  
It fetches real-time weather data using the **OpenWeatherMap API** and displays it with a dynamic UI theme (Sunny, Rainy, Snowy, etc.).

---

## 🚀 Features
- 🌍 Search weather by **city name**
- 🌡️ Displays **temperature, humidity, and conditions**
- 🎨 Dynamic **UI theme** (sun, rain, snow, clouds)
- 💾 Externalized **session storage option** (for containerized deployments)
- 📦 Packaged as a **WAR file** for Tomcat

---

## 🔑 API Key Setup
This app uses the **OpenWeatherMap API**. You must provide your own API key.

1. Register for a free API key:  
   👉 [https://home.openweathermap.org/users/sign_up](https://home.openweathermap.org/users/sign_up)

2. Open Tomcat startup configuration and add:  
   ```sh
   -DOPENWEATHERMAP_API_KEY=your_api_key_here
   ```

    Example in setenv.sh (Linux/macOS):
    ```sh
    export CATALINA_OPTS="$CATALINA_OPTS -DOPENWEATHERMAP_API_KEY=your_api_key_here"
    ```

    Example in setenv.bat (Windows):
    ```sh
    set CATALINA_OPTS=%CATALINA_OPTS% -DOPENWEATHERMAP_API_KEY=your_api_key_here
    ```
3. Restart Tomcat.

---

## ⚡Build & Deploy

1. Build with Maven
```sh
mvn clean package
```

This creates a WAR file at:
```sh
target/weather-app.war
```

2. Deploy to Tomcat
Copy the WAR file to:
```sh
$TOMCAT_HOME/webapps/
```

Start Tomcat:
```sh
$TOMCAT_HOME/bin/startup.sh
```

Access the app:

http://localhost:8080/weather-app/

---

## 🧪 Run Unit Tests
Run JUnit tests:
```sh
mvn test
```
---

## 🛠️ Tech Stack
- Java 11+
- Servlet + JSP
- Apache Tomcat 9+
- Maven
- JUnit
- OpenWeatherMap API

---

## 👨‍💻 Author
Annkur Sharma
💼 DevSecOps | ☁️ Cloud & CI/CD | ⚙️ DevOps Tools