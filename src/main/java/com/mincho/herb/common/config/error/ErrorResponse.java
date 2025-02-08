package com.mincho.herb.common.config.error;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private  int httpStatus; // 상태 코드(ex. 404, 410,..)
    private  String message; // 에러 응답 메시지
    private  HttpErrorType httpErrorType; // 상태 코드의 타입 (ex.BAD_REQUEST)

    public ResponseEntity<Map<String, String>> getResponse(int httpStatus, String message, HttpErrorType httpErrorType){
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("message", message);
        messageMap.put("status", String.valueOf(httpStatus) );
        messageMap.put("StatusCode", httpErrorType.toString());

        return ResponseEntity.status(httpStatus).body(messageMap);
    }

    public ResponseEntity<Map<String, String>> getResponse(int httpStatus, String message, String httpErrorType) {
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("message", message);
        messageMap.put("status", String.valueOf(httpStatus) );
        messageMap.put("StatusCode", httpErrorType);

        return ResponseEntity.status(httpStatus).body(messageMap);
    }

}
