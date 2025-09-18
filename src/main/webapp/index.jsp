<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    com.example.weatherapp.model.WeatherData data =
            (com.example.weatherapp.model.WeatherData) request.getAttribute("weather");
    String theme = (data != null) ? data.getTheme() : "theme-sun";
    String guid = (String) session.getAttribute("GUID");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Weather App</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" href="css/style.css"/>
</head>
<body class="<%= theme %>">
<div class="sky">
    <!-- Animated background layers -->
    <div class="sun"></div>
    <div class="clouds"></div>
    <div class="precip rain-layer"></div>
    <div class="precip snow-layer"></div>
</div>

<div class="container">
    <h1 class="logo">Weather Application</h1>
    <div class="hint">
        To make search more precise put the city's name, comma, 2-letter country code.  
        <br>Example: <b>Paris, FR</b> or <b>Delhi, IN</b>
    </div>
    <br/>
    <form id="searchForm" class="search">
        <input type="text" id="cityInput" placeholder="Search city (e.g., Chandigarh, IN)" autocomplete="off"/>
        <button type="submit">Search</button>
        <button type="button" id="geoBtn" title="Use current location">Use my location</button>
    </form>

    <div id="card" class="card fade">
        <div id="error" class="error" style="display:none;"></div>

        <div id="result" class="result" <%= (data==null) ? "style='display:none;'" : "" %>>
            <% if (data != null) { %>
            <div class="city"><%= data.getCity() %>, <%= data.getCountry() %></div>
            <div class="temp">
                <span class="t"><%= Math.round(data.getTempC()) %>°C</span>
                <span class="feels">feels <%= Math.round(data.getFeelsLikeC()) %>°C</span>
            </div>
            <div class="desc"><%= data.getDescription() %></div>
            <div class="meta">
                <span>Humidity <%= data.getHumidity() %>%</span>
                <span>Wind <%= Math.round(data.getWindKph()) %> km/h</span>
            </div>
            <% } %>
        </div>
    </div>

    <div class="guid-box">
        <div><strong>Session GUID</strong></div>
        <code><%= guid %></code>
        <br>
        <div class="hint">Application Version 1.3</div>
    </div>

    <br/>

    <footer>
        Powered by <a href="https://openweathermap.org/" target="_blank" rel="noopener">OpenWeather</a>
    </footer>
    <div class="hint">Created by Ankur Sharma</div>
</div>

<script src="js/app.js"></script>
<script>
    // When theme updates dynamically via JS, update <body> class
    function updateTheme(newTheme) {
        document.body.className = newTheme;
    }

    // Expose theme from server-side for initial render
    window.initialTheme = "<%= theme %>";
    updateTheme(window.initialTheme);
</script>
</body>
</html>
