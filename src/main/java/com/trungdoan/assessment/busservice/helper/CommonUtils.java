package com.trungdoan.assessment.busservice.helper;

import com.trungdoan.assessment.busservice.model.Location;
import lombok.experimental.UtilityClass;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

@UtilityClass
public class CommonUtils {

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static double calculateBearing(Location loc1, Location loc2) {
        double longitudeDifference = Math.toRadians(loc2.getLongitude() - loc1.getLongitude());
        double latitude1 = Math.toRadians(loc1.getLatitude());
        double latitude2 = Math.toRadians(loc2.getLatitude());

        double x = Math.sin(longitudeDifference) * Math.cos(latitude2);
        double y = Math.cos(latitude1) * Math.sin(latitude2) -
                Math.sin(latitude1) * Math.cos(latitude2) * Math.cos(longitudeDifference);

        double bearing = Math.toDegrees(Math.atan2(x, y));

        // The bearing must be in the range 0-360
        bearing = (bearing + 360) % 360;

        return bearing;
    }
}
