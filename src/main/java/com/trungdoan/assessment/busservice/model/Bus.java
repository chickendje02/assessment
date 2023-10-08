package com.trungdoan.assessment.busservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import com.google.maps.model.Duration;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Bus {

    private Double bearing;

    private String crowdLevel;

    private double lat;

    private double lng;

    private String vehiclePlate;

    private Duration duration;
}
