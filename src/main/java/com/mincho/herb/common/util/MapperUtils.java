package com.mincho.herb.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mincho.herb.domain.herb.domain.HerbSummary;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class MapperUtils {
    private final ObjectMapper objectMapper;

    /**
     * JSON 파일로 부터 데이터를 읽어와서 자바 객체 형태로 변환하는 유틸 메서드 <br/>
     * ★★★ filename 은 확장자를 모두 포함하여 전달해야 한다.(ex. herb.json)
     * */
    public <T> List<T> jsonMapper(String fileName, Class<T> tClass) throws IOException{

            ClassPathResource resource = new ClassPathResource(fileName);
            log.info("resource path: {}", resource);
            return objectMapper.readValue(resource.getInputStream(), objectMapper.getTypeFactory().constructCollectionType(List.class, tClass));
    }

}
