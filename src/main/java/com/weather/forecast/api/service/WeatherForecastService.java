package com.weather.forecast.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.weather.forecast.api.model.Weather;
import com.weather.forecast.api.model.WeatherResponse;
import com.weather.forecast.api.model.response.WeatherForecastResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class WeatherForecastService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherForecastService.class.getName());

    private final String API_KEY;
    private final String API_URL;
    private final WebClient webClient;


    public WeatherForecastService(Environment environment, WebClient webClient) {
        this.webClient = webClient;
        this.API_KEY = environment.getProperty("openweathermap.api.key");
        this.API_URL = environment.getProperty("openweathermap.api.url");
    }

    public WeatherForecastResponse getWeatherForecastByCityId(long cityId) {
        String url = API_URL + "?id=" + cityId + "&appid=" + API_KEY;
        return getWeatherResponse(url);
    }

    public WeatherForecastResponse getWeatherForecastByCityIdAndAppID(String cityId, String appId) {
        String url = API_URL + "?id=" + cityId + "&appid=" + appId;
        return getWeatherResponse(url);
    }

    private WeatherForecastResponse getWeatherResponse(String url) {

        Mono<WeatherResponse> responseWebClient = webClient
                                        .get()
                                        .uri(url)
                                        .retrieve()
                                        .bodyToMono(WeatherResponse.class);

        WeatherResponse weatherResponse = responseWebClient.block();
        List<WeatherResponse.Weather> weatherList = weatherResponse.getList();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentPlus48Hours = now.plusHours(48);
        double maxFeelsLike = Double.MIN_VALUE;
        int maxHumidity = Integer.MIN_VALUE;

        WeatherForecastResponse weatherForecastResponse = new WeatherForecastResponse();
        for( WeatherResponse.Weather weather : weatherList ) {

            String weatherDateStr = weather.getDt_txt();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime currentWeatherLocalDateTime = LocalDateTime.parse(weatherDateStr, formatter);

            if( currentWeatherLocalDateTime.isAfter(now) && currentWeatherLocalDateTime.isBefore( currentPlus48Hours ) ) {

                double feelsLikeCurrent = weather.getMain().getFeels_like();
                if( feelsLikeCurrent > maxFeelsLike ) {
                    maxFeelsLike = feelsLikeCurrent;
                    weatherForecastResponse.setMaxFeelsLike(maxFeelsLike);
                }

                int humidityCurrent = weather.getMain().getHumidity();
                if( humidityCurrent > maxHumidity ) {
                    maxHumidity = humidityCurrent;
                    weatherForecastResponse.setMaxHumidity(maxHumidity);
                }
            }

        }

        return weatherForecastResponse;

    }

    public String getWeatherByCity(String city, String type) {
        String url = String.format("%s?q=%s&appid=%s&units=metric", API_URL, city, API_KEY);
        RestTemplate restTemplate = new RestTemplate();
        Weather response = restTemplate.getForObject(url, Weather.class);

        if (response != null) {
            switch (type) {
                case "temp":
                    if (response.getMain() != null) {
                        return String.format("The temperature in %s is %.2f°C", city, response.getMain().getTemp());
                    }
                    break;
                case "humidity":
                    if (response.getMain() != null) {
                        return String.format("The humidity in %s is %d%%", city, response.getMain().getHumidity());
                    }
                    break;
                case "wind":
                    if (response.getWind() != null) {
                        return String.format("The wind speed in %s is %.2f m/s", city, response.getWind().getSpeed());
                    }
                    break;
                case "sunrise-sunset":
                    if (response.getSys() != null) {
                        long sunriseTimestamp = response.getSys().getSunrise();
                        long sunsetTimestamp = response.getSys().getSunset();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                        String sunriseTime = sdf.format(new Date(sunriseTimestamp * 1000));
                        String sunsetTime = sdf.format(new Date(sunsetTimestamp * 1000));
                        return String.format("In %s, the sunrise is at %s UTC and the sunset is at %s UTC", city, sunriseTime, sunsetTime);
                    }
                    break;
                default:
                    return "Invalid weather type";
            }
        }
        return String.format("Could not retrieve %s data for %s", type, city);
    }

}
