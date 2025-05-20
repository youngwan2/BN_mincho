package com.mincho.herb.global.response.success;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse<T> {
    private  int httpStatus; // 상태 코드(ex. 200, 201,..)
    private  String message; // 에러 응답 메시지
    private  T data;
    private HashMap<String, Integer> meta;
    private HttpSuccessType httpSuccessType; // 상태 코드의 타입 (ex.BAD_REQUEST)

    public SuccessResponse(int httpStatus, String message, T data, HttpSuccessType httpSuccessType){
        this.httpStatus = httpStatus;
        this.message = message;
        this.data = data;
        this.httpSuccessType = httpSuccessType;

    }

    public ResponseEntity<Map<String, String>> getResponse(int httpStatus, String message, HttpSuccessType successType){
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("message", message);
        messageMap.put("status", String.valueOf(httpStatus) );
        messageMap.put("StatusCode", String.valueOf(successType));

        return ResponseEntity.status(httpStatus).body(messageMap);
    }

    public ResponseEntity <SuccessResponse<T>> getResponse(int httpStatus, String message, HttpSuccessType successType, T data){
        SuccessResponse<T> response = new SuccessResponse<>(httpStatus, message,data, successType);
        return ResponseEntity.status(httpStatus).body(response);
    }

    public ResponseEntity <SuccessResponse<T>> getResponse(int httpStatus, String message, HttpSuccessType successType, T data, HashMap<String, Integer> meta){
        SuccessResponse<T> response = new SuccessResponse<>(httpStatus, message,data, meta, successType);
        return ResponseEntity.status(httpStatus).body(response);
    }

}
