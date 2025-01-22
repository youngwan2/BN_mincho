package com.mincho.herb.common.config.success;


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
    private HttpSuccessType httpSuccessType; // 상태 코드의 타입 (ex.BAD_REQUEST)


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

}
