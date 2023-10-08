package com.trungdoan.assessment.busservice.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder
public class ErrorMessage {

    private int statusCode;

    private Date timestamp;

    private String message;

    private String description;
}
