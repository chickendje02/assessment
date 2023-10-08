package com.trungdoan.assessment.busservice.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BusStop extends BaseEntity {

    private double lat;

    private double lng;

    private String name;
}
