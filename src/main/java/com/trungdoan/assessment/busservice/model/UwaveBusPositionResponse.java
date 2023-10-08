package com.trungdoan.assessment.busservice.model;

import lombok.Data;

import java.util.List;

@Data
public class UwaveBusPositionResponse {

    private List<Bus> payload;

    private int status;
}
