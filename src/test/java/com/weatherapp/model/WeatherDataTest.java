package com.example.weatherapp.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class WeatherDataTest {

    @Test
    public void testTheme_whenSnow() {
        WeatherData data = new WeatherData();
        data.setSnow(true);
        assertEquals("theme-snow", data.getTheme());
    }

    @Test
    public void testTheme_whenRain() {
        WeatherData data = new WeatherData();
        data.setRain(true);
        assertEquals("theme-rain", data.getTheme());
    }

    @Test
    public void testTheme_whenCloudsInDescription() {
        WeatherData data = new WeatherData();
        data.setDescription("Scattered clouds");
        assertEquals("theme-clouds", data.getTheme());
    }

    @Test
    public void testTheme_defaultSun() {
        WeatherData data = new WeatherData();
        data.setDescription("Clear sky");
        assertEquals("theme-sun", data.getTheme());
    }

    @Test
    public void testTheme_nullDescriptionDefaultsToSun() {
        WeatherData data = new WeatherData();
        assertEquals("theme-sun", data.getTheme());
    }

    @Test
    public void testTheme_descriptionCaseInsensitive() {
        WeatherData data = new WeatherData();
        data.setDescription("CLOUDY with rain");
        // "cloud" substring should be detected case-insensitively
        assertEquals("theme-clouds", data.getTheme());
    }

    @Test
    public void testSettersAndGetters() {
        WeatherData data = new WeatherData();

        data.setCity("Delhi");
        data.setCountry("IN");
        data.setDescription("Sunny");
        data.setIcon("01d");
        data.setTempC(30.5);
        data.setFeelsLikeC(32.0);
        data.setHumidity(70);
        data.setWindKph(15.2);
        data.setRain(true);
        data.setSnow(false);

        assertEquals("Delhi", data.getCity());
        assertEquals("IN", data.getCountry());
        assertEquals("Sunny", data.getDescription());
        assertEquals("01d", data.getIcon());
        assertEquals(30.5, data.getTempC(), 0.001);
        assertEquals(32.0, data.getFeelsLikeC(), 0.001);
        assertEquals(70, data.getHumidity());
        assertEquals(15.2, data.getWindKph(), 0.001);
        assertTrue(data.isRain());
        assertFalse(data.isSnow());
    }

    @Test
    public void testSwitchingFlags_prioritySnowOverRain() {
        WeatherData data = new WeatherData();
        data.setRain(true);
        data.setSnow(true);  // snow should take priority
        assertEquals("theme-snow", data.getTheme());
    }

    @Test
    public void testSwitchingFlags_priorityRainOverClouds() {
        WeatherData data = new WeatherData();
        data.setDescription("cloudy");
        data.setRain(true); // rain should override cloud
        assertEquals("theme-rain", data.getTheme());
    }

    @Test
    public void testEmptyDescriptionDefaultsToSun() {
        WeatherData data = new WeatherData();
        data.setDescription("");
        assertEquals("theme-sun", data.getTheme());
    }

    @Test
    public void testNumericPropertiesWithZeroValues() {
        WeatherData data = new WeatherData();
        data.setTempC(0.0);
        data.setFeelsLikeC(0.0);
        data.setWindKph(0.0);
        data.setHumidity(0);

        assertEquals(0.0, data.getTempC(), 0.001);
        assertEquals(0.0, data.getFeelsLikeC(), 0.001);
        assertEquals(0.0, data.getWindKph(), 0.001);
        assertEquals(0, data.getHumidity());
    }

    @Test
    public void testNegativeTemperatureAndWindValues() {
        WeatherData data = new WeatherData();
        data.setTempC(-5.5);
        data.setFeelsLikeC(-10.0);
        data.setWindKph(-1.0);

        assertEquals(-5.5, data.getTempC(), 0.001);
        assertEquals(-10.0, data.getFeelsLikeC(), 0.001);
        assertEquals(-1.0, data.getWindKph(), 0.001);
    }

    @Test
    public void testTheme_whenCloudsCaseInsensitive() {
        WeatherData data = new WeatherData();
        data.setDescription("CLOUDY sky");
        assertEquals("theme-clouds", data.getTheme());
    }

    @Test
    public void testTheme_priorityRainOverClouds() {
        WeatherData data = new WeatherData();
        data.setRain(true);
        data.setDescription("clouds all around");
        assertEquals("theme-rain", data.getTheme()); // rain wins
    }

        @Test
    public void testExtremeValues() {
        WeatherData data = new WeatherData();

        data.setTempC(-15.0);
        data.setHumidity(0);
        data.setWindKph(200.0);

        assertEquals(-15.0, data.getTempC(), 0.001);
        assertEquals(0, data.getHumidity());
        assertEquals(200.0, data.getWindKph(), 0.001);
    }

}
