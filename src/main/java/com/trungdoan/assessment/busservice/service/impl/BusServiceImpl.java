package com.trungdoan.assessment.busservice.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.trungdoan.assessment.busservice.cache.BusCache;
import com.trungdoan.assessment.busservice.cache.CacheModel;
import com.trungdoan.assessment.busservice.configuration.UwaveConfig;
import com.trungdoan.assessment.busservice.google.GoogleMapService;
import com.trungdoan.assessment.busservice.helper.CommonUtils;
import com.trungdoan.assessment.busservice.helper.RestClientSupport;
import com.trungdoan.assessment.busservice.model.*;
import com.trungdoan.assessment.busservice.service.BusService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.trungdoan.assessment.busservice.constant.CommonConstant.KEY_CACHE_LIST_BUS_LINES;
import static com.trungdoan.assessment.busservice.constant.CommonConstant.KEY_CACHE_LIST_BUS_STOP;

@Service
@Log4j2
public class BusServiceImpl extends RestClientSupport implements BusService {

    private static final String URL_BUS_LINE = "busLines";
    private static final String URL_BUS_Position = "busPositions/";
    private static final ExecutorService executors = Executors.newFixedThreadPool(8);


    @Autowired
    UwaveConfig uwaveConfig;

    @Autowired
    GoogleMapService googleMapService;

    @Override
    public List<BusLine> getBusLine() {
        CacheModel<List<BusLine>> cache = BusCache.getData(KEY_CACHE_LIST_BUS_LINES);
        if (Objects.nonNull(cache)) {
            return cache.getData();
        }
        UwaveBusLineResponse response = get(uwaveConfig.getRestApiUrl() + URL_BUS_LINE, new TypeReference<UwaveBusLineResponse>() {
        });
        handlePutToCache(response);
        return response.getPayload();
    }

    @Override
    public List<Bus> getRunningBusesOfBusLine(int busLineId) {
        UwaveBusPositionResponse response = get(uwaveConfig.getRestApiUrl() + URL_BUS_Position + busLineId, new TypeReference<UwaveBusPositionResponse>() {
        });
        return response.getPayload();
    }

    @Override
    public Map<String, Object> getIncomingBusesOfBusStop(String busStopId) {
        Map<String, Object> result = new ConcurrentHashMap<>();
        try {
            CacheModel<List<BusLine>> cache = BusCache.getData(KEY_CACHE_LIST_BUS_LINES);
            List<BusLine> busLines;
            if (Objects.isNull(cache)) {
                busLines = this.getBusLine();
                executors.awaitTermination(1000, TimeUnit.MICROSECONDS);
            } else {
                busLines = cache.getData();
            }
            CacheModel<List<BusStop>> cacheBusStop = BusCache.getData(KEY_CACHE_LIST_BUS_STOP);
            BusStop busStop = cacheBusStop.getData()
                    .stream()
                    .filter(busStopItem -> busStopId.equalsIgnoreCase(busStopItem.getId()))
                    .findFirst()
                    .get();

            // Get List
            List<BusLine> listBusLineHasBusStopId =
                    busLines.stream()
                            .filter(item -> item.getBusStops()
                                    .stream()
                                    .anyMatch(busStopItem -> busStopId.equalsIgnoreCase(busStopItem.getId()))
                            )
                            .collect(Collectors.toList());

            listBusLineHasBusStopId.stream().forEach(busLine -> {
                List<Bus> runningBus = this.getRunningBusesOfBusLine(Integer.parseInt(busLine.getId()));
                List<Bus> list = new ArrayList<>();
                runningBus.forEach(bus -> {
                    if (checkIfBusIsHeadingToBusStop(bus, busStop)) {
                        list.add(bus);
                    }
                });
                if (!list.isEmpty()) {
                    googleMapService.getDuration(list, busStop);
                    result.putIfAbsent(busLine.getFullName(), list);
                }
            });
        } catch (Exception e) {
            log.error("Exception happened ", e);
        }
        return result;
    }

    private void handlePutToCache(UwaveBusLineResponse response) {
        executors.submit(() -> {
            List<BusLine> listBusLines = response.getPayload();
            List<BusStop> listBusStop = listBusLines.stream().flatMap(item -> item.getBusStops().stream()).filter(CommonUtils.distinctByKey(BusStop::getId)).collect(Collectors.toList());
            BusCache.putData(KEY_CACHE_LIST_BUS_LINES, listBusLines, BusCache.DEFAULT_EXPIRED_TIME * 6 * 12);
            BusCache.putData(KEY_CACHE_LIST_BUS_STOP, listBusStop, 0L, false);
        });
    }

    /*
        This isn't  an accurate check
        // So
     */
    private boolean checkIfBusIsHeadingToBusStop(Bus bus, BusStop busStop) {
        //Todo later
//        Location busLocation = new Location(bus.getLat(), bus.getLng());
//        Location busStopLocation = new Location(busStop.getLat(), busStop.getLng());
//        double bearingToBusStop = CommonUtils.calculateBearing(busLocation, busStopLocation);
//        return Math.abs(bus.getBearing() - bearingToBusStop) < 10;
        return true;
    }
}
