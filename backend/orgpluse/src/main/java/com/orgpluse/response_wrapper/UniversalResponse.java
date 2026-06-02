package com.orgpluse.response_wrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UniversalResponse {

    @Autowired
    private ResponseWrapper responseWrapper;

    public ResponseEntity<ResponseWrapper> send(String message, Object data, HttpStatus status) {
        responseWrapper.setMessage(message);
        responseWrapper.setData(data);
        return new ResponseEntity<>(responseWrapper, status);
    }

}
