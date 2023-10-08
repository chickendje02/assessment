package com.trungdoan.assessment.busservice.cache;

import lombok.Data;

@Data
public class CacheModel<T> {

    private T data;

    private Long expiredTime;

    private boolean isAllowRemoved;
}

