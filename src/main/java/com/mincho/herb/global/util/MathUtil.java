package com.mincho.herb.global.util;

import org.springframework.stereotype.Component;

@Component
public class MathUtil {
    public static double getGrowthRate(Long previousCount, Long currentCount) {
        double growthRate = 0.0;
        // 이전이 존재한다면,
        if (previousCount > 0) {
            growthRate = ((double)(currentCount - previousCount) / previousCount) * 100;
        }  else if(previousCount ==0 && currentCount >0){
            growthRate = 100.0; // 이전이 없고 이번에만 있는 경우
        } else if(previousCount ==0 && currentCount ==0){
            growthRate = 0.0; // 이전과 이번 모두 없는 경우
        } else {
            growthRate = -100.0; // 이전에만  있었던 경우
        }
        return Math.round(growthRate * 100.0) / 100.0; // 소수점 둘째자리까지 반올림
    }
}
