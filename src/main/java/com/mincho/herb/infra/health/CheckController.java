package com.mincho.herb.infra.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class CheckController {

    @Value("${server.env}") // application.yml 의 server.env 의 값을 읽어온다.
    private String env;

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.serverAddress}")
    private String serverAddress;

    @Value("${server.serverName}")
    private String serverName;

    @GetMapping("/ch/connection")
    public ResponseEntity<?> hcConnection(){
        Map<String,String> responseMap = new HashMap<>();
        responseMap.put("serverName", serverName);
        responseMap.put("serverAddress", serverAddress);
        responseMap.put("serverPort", serverPort);
        responseMap.put("env", env);

        return ResponseEntity.ok(responseMap);
    }

    @GetMapping("/ch/env")
    public ResponseEntity<?> hcEnv(){
        return ResponseEntity.ok(env);
    }
}
