package com.example.weather;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherData {
    private String mTemperature, mIcon, mCity, mWeatherType;
    private int mCondition;

    public static WeatherData fromJson(JSONObject jsonObject) {
        try {
            WeatherData weatherData = new WeatherData();
            weatherData.mCity = jsonObject.getString("name");
            weatherData.mCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherData.mWeatherType = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            weatherData.mIcon = upWeatherIcon(weatherData.mCondition);

            double tempResult = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;
            int roundedValue = (int) Math.rint(tempResult);
            weatherData.mTemperature = Integer.toString(roundedValue);
            return weatherData;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static String upWeatherIcon(int condition) {
        if (condition >= 0 && condition <= 300) {
            return "thunder1";
        } else if (condition >= 300 && condition <= 500) {
            return "light_rain";
        } else if (condition >= 500 && condition <= 600) {
            return "shower";
        } else if (condition >= 600 && condition <= 700) {
            return "snow2";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition <= 800) {
            return "overcast";
        } else if (condition == 800) {
            return "sun";
        } else if (condition >= 801 && condition <= 804) {
            return "cloudy";
        } else if (condition >= 900 && condition <= 902) {
            return "thunder1";
        } else if (condition == 903) {
            return "snow1";
        } else if (condition == 904) {
            return "sun";
        } else if (condition >= 905 && condition <= 1000) {
            return "thunderstorm2";
        }
        return "dunno";
    }

    public String getTemperature() {
        return mTemperature + "°C";
    }

    public String getIcon() {
        return mIcon;
    }

    public String getCity() {
        return mCity;
    }

    public String getWeatherType() {
        return mWeatherType;
    }
}
