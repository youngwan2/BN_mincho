package com.mincho.herb.global.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;

/**
 * 이 친구는 향후 EmbeddingEntity 에서 임베딩 컬럼에 데이터를 저장 및 반환할 때 사용 됨.
 * */
@Converter
public class FloatArrayVectorConverter implements AttributeConverter<float[], String> {

    // 엔티티를 데이터베이스 컬럼으로 변환
    @Override
    public String convertToDatabaseColumn(float[] floats) {
        if(floats == null) return null;
        return Arrays.toString(floats); // 벡터는 문자열 배열로 저장되어야 함으로 변환해주는 것임
    }

    // 데이터베이스 데이터를 엔티티 속성으로 변환
    @Override
    public float[] convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        dbData = dbData.replaceAll("[\\[\\]\\s]", "");
        String[] parts = dbData.split(",");
        float[] result = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Float.parseFloat(parts[i]);
        }
        return result;
    }
}
