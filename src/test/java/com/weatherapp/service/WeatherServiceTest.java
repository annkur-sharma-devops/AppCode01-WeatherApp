package com.example.weatherapp.service;

import com.example.weatherapp.model.WeatherData;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class WeatherServiceTest {

    @Test
    public void parse_valid_openweather_snapshot() {
        String sample = "{\n" +
                "  \"name\": \"London\",\n" +
                "  \"sys\": {\"country\": \"GB\"},\n" +
                "  \"weather\": [{\"description\": \"light rain\", \"icon\": \"10d\"}],\n" +
                "  \"main\": {\"temp\": 18.5, \"feels_like\": 17.8, \"humidity\": 72},\n" +
                "  \"wind\": {\"speed\": 3.2}\n" +
                "}";
        WeatherService svc = new WeatherService();
        WeatherData d = svc.parse(sample);

        assertEquals("London", d.getCity());
        assertEquals("GB", d.getCountry());
        assertEquals("light rain", d.getDescription());
        assertEquals("10d", d.getIcon());
        assertEquals(18.5, d.getTempC(), 0.0001);
        assertEquals(17.8, d.getFeelsLikeC(), 0.0001);
        assertEquals(72, d.getHumidity());
        assertEquals(3.2 * 3.6, d.getWindKph(), 0.0001);
        assertTrue(d.isRain());
        assertFalse(d.isSnow());
    }

    @Test
    public void parse_json_with_snow() {
        String sample = "{ \"name\": \"Oslo\", \"weather\": [{\"description\": \"heavy snow\", \"icon\": \"13d\"}] }";
        WeatherService svc = new WeatherService();
        WeatherData d = svc.parse(sample);
        assertTrue(d.isSnow());
        assertFalse(d.isRain());
    }

    @Test
    public void parse_json_with_rain_field_only() {
        String sample = "{ \"name\": \"Mumbai\", \"rain\": {\"1h\": 5.0}, \"weather\": [{\"description\": \"clear sky\"}] }";
        WeatherService svc = new WeatherService();
        WeatherData d = svc.parse(sample);
        assertTrue(d.isRain());   // inferred from "rain" field
        assertFalse(d.isSnow());
    }

    @Test
    public void parse_json_with_snow_field_only() {
        String sample = "{ \"name\": \"Alps\", \"snow\": {\"1h\": 2.0}, \"weather\": [{\"description\": \"clear sky\"}] }";
        WeatherService svc = new WeatherService();
        WeatherData d = svc.parse(sample);
        assertTrue(d.isSnow());   // inferred from "snow" field
        assertFalse(d.isRain());
    }

    @Test
    public void parse_json_with_missing_fields() {
        String sample = "{ \"main\": {}, \"wind\": {} }";
        WeatherService svc = new WeatherService();
        WeatherData d = svc.parse(sample);

        assertNull(d.getCity());
        assertNull(d.getCountry());
        assertNull(d.getDescription());
        assertEquals(0.0, d.getTempC(), 0.0001);
        assertEquals(0, d.getHumidity());
    }

    @Test
    public void parse_json_with_negative_wind_speed() {
        String sample = "{ \"wind\": {\"speed\": -2.0} }";
        WeatherService svc = new WeatherService();
        WeatherData d = svc.parse(sample);
        assertEquals(-7.2, d.getWindKph(), 0.0001);
    }

    @Test
    public void parse_invalid_json_shouldThrow() {
        WeatherService svc = new WeatherService();
        try {
            svc.parse("{ not-json }");
            fail("Expected exception");
        } catch (Exception e) {
            assertTrue(e instanceof com.google.gson.JsonSyntaxException);
        }
    }

    @Test
    public void apiKey_missing_shouldThrowException() throws Exception {
        if (System.getenv("OPENWEATHER_API_KEY") != null) {
            System.out.println("Skipping test: OPENWEATHER_API_KEY is set.");
            return;
        }
        System.clearProperty("OPENWEATHER_API_KEY");

        try {
            new WeatherService().getByCity("Delhi");
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("Missing OpenWeather API key"));
        }
    }

    @Test
    public void apiKey_from_systemProperty() throws Exception {
        if (System.getenv("OPENWEATHER_API_KEY") != null) {
            System.out.println("Skipping test: OPENWEATHER_API_KEY is set.");
            return;
        }

        System.setProperty("OPENWEATHER_API_KEY", "dummy123");

        Method apiKeyMethod = WeatherService.class.getDeclaredMethod("apiKey");
        apiKeyMethod.setAccessible(true);
        String key = (String) apiKeyMethod.invoke(null);
        assertEquals("dummy123", key);
    }

    @Test
    public void encode_shouldEscapeSpaces() throws Exception {
        Method encodeMethod = WeatherService.class.getDeclaredMethod("encode", String.class);
        encodeMethod.setAccessible(true);
        String result = (String) encodeMethod.invoke(null, "New York");
        assertEquals("New+York", result);
    }

    @Test
    public void safeString_withNullAndJsonNull() throws Exception {
        Method safeString = WeatherService.class.getDeclaredMethod("safeString", com.google.gson.JsonElement.class);
        safeString.setAccessible(true);

        assertNull(safeString.invoke(null, (Object) null));
        assertNull(safeString.invoke(null, JsonNull.INSTANCE));
        assertEquals("abc", safeString.invoke(null, JsonParser.parseString("\"abc\"")));
    }

    @Test
    public void url_shouldReturnInput() throws Exception {
        Method urlMethod = WeatherService.class.getDeclaredMethod("url", String.class);
        urlMethod.setAccessible(true);
        String result = (String) urlMethod.invoke(null, "http://example.com");
        assertEquals("http://example.com", result);
    }
}
