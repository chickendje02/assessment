package com.trungdoan.assessment.busservice.service;

import com.trungdoan.assessment.busservice.model.Bus;
import com.trungdoan.assessment.busservice.model.BusLine;

import java.util.List;
import java.util.Map;

public interface BusService {

    List<BusLine> getBusLine();

    List<Bus> getRunningBusesOfBusLine(int busLineId);

    Map<String, Object> getIncomingBusesOfBusStop(String busStopId);
}
