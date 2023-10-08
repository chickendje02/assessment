package com.trungdoan.assessment.busservice.cache;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class BusCache {
    /*
        use to get cache map for debugging
     */
    private static final Map<String, CacheModel> cacheMap = new HashMap<String, CacheModel>();

    /**
     * 1000L*60*60*24*30 equals to 1 month which has 30 days
     * To convert to timestamp just multiply it with 100
     * DEFAULT EXPIRED TIME will be 10minutes
     */
    public static final long DEFAULT_EXPIRED_TIME = 600_000L;

    /*
      Since this class is used for cache, so prevent from creating instance
     */
    private BusCache() {

    }

    public static <T> void putData(String key, T data) {
        putData(key, data, DEFAULT_EXPIRED_TIME);
    }

    public static <T> void putData(String key, T data, Long expiredTime) {
        Long timeExpired = Optional.ofNullable(expiredTime).orElse(DEFAULT_EXPIRED_TIME);
        CacheModel<T> cacheData = new CacheModel<>();
        cacheData.setData(data);
        cacheData.setExpiredTime(System.currentTimeMillis() + timeExpired);
        cacheMap.put(key, cacheData);
    }

    public static <T> void putData(String key, T data, Long expiredTime, boolean isAllowRemoved) {
        Long timeExpired = Optional.ofNullable(expiredTime).orElse(DEFAULT_EXPIRED_TIME);
        CacheModel<T> cacheData = new CacheModel<>();
        cacheData.setData(data);
        cacheData.setExpiredTime(System.currentTimeMillis() + timeExpired);
        cacheData.setAllowRemoved(isAllowRemoved);
        cacheMap.put(key, cacheData);
    }

    public static CacheModel getData(String key) {
        CacheModel data = cacheMap.get(key);
        if (Objects.isNull(data)) {
            return null;
        }
        if (data.isAllowRemoved() && System.currentTimeMillis() >= data.getExpiredTime()) {
            cacheMap.remove(key);
            return null;
        } else {
            return data;
        }
    }

    public static <T> void updateData(String key, T data) {
        CacheModel<T> cachesData = new CacheModel<>();
        cachesData.setData(data);
        cachesData.setExpiredTime(System.currentTimeMillis() + DEFAULT_EXPIRED_TIME);
        cacheMap.put(key, cachesData);
    }

    public static <T> void removeData(String key) {
        cacheMap.remove(key);
    }

    public static Map<String, CacheModel> getCacheMap() {
        return cacheMap;
    }
}
