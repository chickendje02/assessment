package com.trungdoan.assessment.busservice.exception;

import lombok.Data;

@Data
public class CommonBusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int code;

    public CommonBusinessException(String msg) {
        super(msg);
    }

    public CommonBusinessException(String msg, int code) {
        super(msg);
        this.code = code;
    }
}
