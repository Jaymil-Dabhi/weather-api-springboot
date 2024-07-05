package com.weather.forecast.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sys {
    private String pod;

    public long getSunrise() {
        return 0;
    }

    public long getSunset() {
        return 0;
    }
}
