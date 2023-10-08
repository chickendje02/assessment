package com.trungdoan.assessment.busservice.configuration;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "test-uwave")
@Component
@Data
public class UwaveConfig {

    private String restApiUrl;
}
