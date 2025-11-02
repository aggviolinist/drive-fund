package com.drivefundproject.drive_fund.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHandler {

    public static <T> ResponseEntity<Object> generateResponse(HttpStatus httpStatus, String message, T responseObj) {
        Map<String, Object> map = new LinkedHashMap<>();
        int statusFlag = httpStatus.is2xxSuccessful() ? 1 : 0;
        map.put("status", statusFlag);
        map.put("message", message);
        map.put("data", responseObj);

        return new ResponseEntity<>(map, httpStatus);
    }
}