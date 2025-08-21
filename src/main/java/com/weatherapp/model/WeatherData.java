package com.example.weatherapp.model;

public class WeatherData {
    private String city;
    private String country;
    private String description;
    private String icon;
    private double tempC;
    private double feelsLikeC;
    private int humidity;
    private double windKph;
    private boolean rain;
    private boolean snow;

    public WeatherData() {}

    public String getTheme() {
        if (snow) return "theme-snow";
        if (rain) return "theme-rain";
        if (description != null && description.toLowerCase().contains("cloud"))
            return "theme-clouds";
        return "theme-sun";
    }

    // Getters and setters
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public double getTempC() { return tempC; }
    public void setTempC(double tempC) { this.tempC = tempC; }
    public double getFeelsLikeC() { return feelsLikeC; }
    public void setFeelsLikeC(double feelsLikeC) { this.feelsLikeC = feelsLikeC; }
    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }
    public double getWindKph() { return windKph; }
    public void setWindKph(double windKph) { this.windKph = windKph; }
    public boolean isRain() { return rain; }
    public void setRain(boolean rain) { this.rain = rain; }
    public boolean isSnow() { return snow; }
    public void setSnow(boolean snow) { this.snow = snow; }
}
