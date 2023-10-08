package com.trungdoan.assessment.busservice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "google-map.api")
@Component
@Data
public class GoogleMapApiConfig {

    private String key;

    private String googleMapUrl;
}
