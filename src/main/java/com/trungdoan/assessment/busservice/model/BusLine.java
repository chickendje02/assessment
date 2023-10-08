package com.trungdoan.assessment.busservice.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class BusLine extends BaseEntity {

    private String fullName;

    private String shortName;

    private String orgins;

    private List<BusStop> busStops;

    private List<List<Double>> path;
}
