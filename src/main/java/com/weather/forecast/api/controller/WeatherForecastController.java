package com.weather.forecast.api.controller;

import com.weather.forecast.api.model.Weather;
import com.weather.forecast.api.model.WeatherResponse;
import com.weather.forecast.api.model.request.City;
import com.weather.forecast.api.model.response.WeatherForecastResponse;
import com.weather.forecast.api.service.WeatherForecastService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@RestController
@RequestMapping("/api")
public class WeatherForecastController {


    private static final Logger Log = LoggerFactory.getLogger(WeatherForecastController.class);
    @Autowired
    private WeatherForecastService weatherForecastService;

    public WeatherForecastController(WeatherForecastService weatherService) {
        this.weatherForecastService = weatherForecastService;
    }

    private String apiKey;

    private String apiUrl;

//    @Value("${weather.api.forecast.url}")
//    private String forecastApiUrl;

    @Operation(summary = "Get Weather Forecast By City Id provided in http body")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved maxFeelsLike and maxHumidity"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found"),
            @ApiResponse(responseCode = "500", description = "City value can not be null")
    })
    @GetMapping("/city/{city}")
    public String getWeatherByCity(@PathVariable String city, @PathVariable String type) {
        return weatherForecastService.getWeatherByCity(city,type);
    }

    @GetMapping("/temperature/{city}")
    public String getTemperatureByCity(@PathVariable String city) {
        String url = String.format("%s?q=%s&appid=%s&units=metric", apiUrl, city, apiKey);
        RestTemplate restTemplate = new RestTemplate();
        try {
            Weather response = restTemplate.getForObject(url, Weather.class);
            if (response != null && response.getMain() != null) {
                return String.format("The temperature in %s is %.2f°C", city, response.getMain().getTemp());
            } else {
                return String.format("Could not retrieve temperature for %s", city);
            }
        } catch (Exception e) {
            return String.format("An error occurred while retrieving the temperature for %s: %s", city, e.getMessage());
        }
    }

    @GetMapping("/humidity/{city}")
    public String getHumidityByCity(@PathVariable String city) {
        String url = String.format("%s?q=%s&appid=%s&units=metric", apiUrl, city, apiKey);
        RestTemplate restTemplate = new RestTemplate();
        Weather response = restTemplate.getForObject(url, Weather.class);

        if (response != null && response.getMain() != null) {
            return String.format("The humidity in %s is %d%%", city, response.getMain().getHumidity());
        } else {
            return String.format("Could not retrieve humidity for %s", city);
        }
    }

    @GetMapping("/wind/{city}")
    public String getWindByCity(@PathVariable String city) {
        String url = String.format("%s?q=%s&appid=%s&units=metric", apiUrl, city, apiKey);
        RestTemplate restTemplate = new RestTemplate();
        Weather response = restTemplate.getForObject(url, Weather.class);

        if (response != null && response.getWind() != null) {
            return String.format("The wind speed in %s is %.2f m/s", city, response.getWind().getSpeed());
        } else {
            return String.format("Could not retrieve wind data for %s", city);
        }
    }

    @GetMapping("/forecast/{city}")
    public String getForecastByCity(@PathVariable String city) {
        String url = String.format("%s?q=%s&appid=%s&units=metric", apiUrl, city, apiKey);
        RestTemplate restTemplate = new RestTemplate();
        WeatherResponse response = restTemplate.getForObject(url, WeatherResponse.class);

        if (response != null && response.getList() != null) {
            StringBuilder forecast = new StringBuilder(String.format("5-day forecast for %s:\n", city));
            for (WeatherResponse.Weather forecastItem : response.getList()) {
                forecast.append(String.format("Date: %s, Temperature: %.2f°C, Humidity: %d%%, Wind Speed: %.2f m/s\n",
                        forecastItem.getDt_txt(), forecastItem.getMain().getTemp(), forecastItem.getMain().getHumidity(), forecastItem.getWind().getSpeed()));
            }
            return forecast.toString();
        } else {
            return String.format("Could not retrieve forecast data for %s", city);
        }
    }

    @GetMapping("/sunrise-sunset/{city}")
    public String getSunriseSunsetByCity(@PathVariable String city) {
        String url = String.format("%s?q=%s&appid=%s&units=metric", apiUrl, city, apiKey);
        RestTemplate restTemplate = new RestTemplate();
        Weather response = restTemplate.getForObject(url, Weather.class);

        if (response != null && response.getSys() != null) {
            long sunriseTimestamp = response.getSys().getSunrise();
            long sunsetTimestamp = response.getSys().getSunset();

            // Convert timestamps to readable date format
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String sunriseTime = sdf.format(new Date(sunriseTimestamp * 1000));
            String sunsetTime = sdf.format(new Date(sunsetTimestamp * 1000));

            return String.format("In %s, the sunrise is at %s UTC and the sunset is at %s UTC", city, sunriseTime, sunsetTime);
        } else {
            return String.format("Could not retrieve sunrise and sunset times for %s", city);
        }
    }
}