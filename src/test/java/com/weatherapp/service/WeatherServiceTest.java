package com.example.weatherapp.service;

import com.example.weatherapp.model.WeatherData;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests JSON parsing only (no live HTTP call).
 */
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
        assertEquals("theme-rain", d.getTheme());
    }
}
