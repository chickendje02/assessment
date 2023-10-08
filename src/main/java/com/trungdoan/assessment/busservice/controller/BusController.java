package com.trungdoan.assessment.busservice.controller;

import com.trungdoan.assessment.busservice.cache.BusCache;
import com.trungdoan.assessment.busservice.model.Bus;
import com.trungdoan.assessment.busservice.model.BusLine;
import com.trungdoan.assessment.busservice.service.BusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.trungdoan.assessment.busservice.constant.CommonConstant.KEY_CACHE_LIST_BUS_LINES;
import static com.trungdoan.assessment.busservice.constant.CommonConstant.KEY_CACHE_LIST_BUS_STOP;

@RestController
@RequestMapping("/v1/buses")
public class BusController {

    @Autowired
    BusService busService;

    @GetMapping
    public ResponseEntity<List<BusLine>> getListBusLine() {
        return ResponseEntity.ok(busService.getBusLine());
    }

    @GetMapping("lines/{busLinesId}/positions")
    public ResponseEntity<List<Bus>> getRunningBusesOfBusLine(@PathVariable int busLinesId) {
        return ResponseEntity.ok(busService.getRunningBusesOfBusLine(busLinesId));
    }

    @GetMapping("/bus-stop/{busStopId}")
    public ResponseEntity<Map<String, Object>> getIncomingBusesOfBusStop(@PathVariable String busStopId) {
        return ResponseEntity.ok(busService.getIncomingBusesOfBusStop(busStopId));
    }
}
