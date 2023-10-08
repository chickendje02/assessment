package com.trungdoan.assessment.busservice.model;

import lombok.Data;

import java.util.List;

@Data
public class UwaveBusLineResponse {

    private List<BusLine> payload;

    private int status;
}
