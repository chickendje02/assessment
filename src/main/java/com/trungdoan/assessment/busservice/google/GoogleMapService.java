package com.trungdoan.assessment.busservice.google;


import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import com.trungdoan.assessment.busservice.configuration.GoogleMapApiConfig;
import com.trungdoan.assessment.busservice.model.Bus;
import com.trungdoan.assessment.busservice.model.BusStop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoogleMapService {

    @Autowired
    GoogleMapApiConfig googleMapApiConfig;
    private static GeoApiContext context;

    public void getDuration(List<Bus> list, BusStop busStop) {
        list.forEach(bus -> {
            try {
                LatLng location = new LatLng(bus.getLat(), bus.getLng());
                DirectionsResult resultDirection = DirectionsApi.newRequest(getContext())
                        .origin(location)
                        .destination(String.format("%f,%f", busStop.getLat(), busStop.getLng()))
                        .await();
                bus.setDuration(resultDirection.routes[0].legs[0].duration);
            } catch (Exception e) {

            }
        });
    }


    public GeoApiContext getContext() {
        if (context == null) {
            context = new GeoApiContext.Builder()
                    .apiKey(googleMapApiConfig.getKey())
                    .build();
        }
        return context;
    }


}
