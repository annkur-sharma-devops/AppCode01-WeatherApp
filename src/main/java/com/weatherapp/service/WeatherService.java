package com.example.weatherapp.service;

import com.example.weatherapp.model.WeatherData;
import com.google.gson.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Calls OpenWeatherMap Current Weather API and parses a minimal WeatherData.
 * API key is read from env OR system property named OPENWEATHER_API_KEY.
 */
public class WeatherService {

    private static final String BASE = "https://api.openweathermap.org/data/2.5/weather";

    public WeatherData getByCity(String city) throws Exception {
        String apiKey = apiKey();
        String q = url(BASE + "?q=" + encode(city) + "&appid=" + encode(apiKey) + "&units=metric");
        String json = httpGet(q);
        return parse(json);
    }

    public WeatherData getByLatLon(double lat, double lon) throws Exception {
        String apiKey = apiKey();
        String q = url(BASE + "?lat=" + lat + "&lon=" + lon + "&appid=" + encode(apiKey) + "&units=metric");
        String json = httpGet(q);
        return parse(json);
    }

    // ---- Helpers ----

    /* package-private */ WeatherData parse(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        WeatherData d = new WeatherData();
        if (root.has("name")) d.setCity(safeString(root.get("name")));
        if (root.has("sys")) {
            JsonObject sys = root.getAsJsonObject("sys");
            if (sys.has("country")) d.setCountry(safeString(sys.get("country")));
        }

        if (root.has("weather")) {
            JsonArray arr = root.getAsJsonArray("weather");
            if (arr.size() > 0) {
                JsonObject w = arr.get(0).getAsJsonObject();
                d.setDescription(safeString(w.get("description")));
                d.setIcon(safeString(w.get("icon")));
            }
        }

        if (root.has("main")) {
            JsonObject m = root.getAsJsonObject("main");
            if (m.has("temp")) d.setTempC(m.get("temp").getAsDouble());
            if (m.has("feels_like")) d.setFeelsLikeC(m.get("feels_like").getAsDouble());
            if (m.has("humidity")) d.setHumidity(m.get("humidity").getAsInt());
        }
        if (root.has("wind")) {
            JsonObject w = root.getAsJsonObject("wind");
            if (w.has("speed")) {
                double mps = w.get("speed").getAsDouble();
                d.setWindKph(mps * 3.6);
            }
        }

        boolean rain = root.has("rain");
        boolean snow = root.has("snow");
        // Also infer rain/snow from description
        String desc = d.getDescription() == null ? "" : d.getDescription().toLowerCase();
        rain = rain || desc.contains("rain") || desc.contains("drizzle") || desc.contains("thunder");
        snow = snow || desc.contains("snow") || desc.contains("sleet");
        d.setRain(rain);
        d.setSnow(snow);

        return d;
    }

    private static String httpGet(String url) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setConnectTimeout(8000);
        con.setReadTimeout(8000);
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");
        int code = con.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (code >= 200 && code < 300) ? con.getInputStream() : con.getErrorStream(),
                StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line; while ((line = br.readLine()) != null) sb.append(line);
        br.close(); con.disconnect();
        if (code < 200 || code >= 300) {
            throw new RuntimeException("OpenWeather error " + code + ": " + sb);
        }
        return sb.toString();
    }

    private static String encode(String s) throws Exception {
        return java.net.URLEncoder.encode(s, "UTF-8");
    }

    private static String url(String s) { return s; }

    private static String apiKey() {
        String k = System.getenv("OPENWEATHER_API_KEY");
        if (k == null || k.trim().isEmpty())
            k = System.getProperty("OPENWEATHER_API_KEY");
        if (k == null || k.trim().isEmpty())
            throw new IllegalStateException("Missing OpenWeather API key. Set env or -DOPENWEATHER_API_KEY");
        return k;
    }

    private static String safeString(JsonElement e) {
        return (e == null || e.isJsonNull()) ? null : e.getAsString();
    }
}
