package com.mincho.herb.global.entity;

import lombok.Getter;

@Getter
public enum TagTypeEnum {
    EFFECT("효능"),
    SIDE_EFFECT("부작용");

    private final String description;

    TagTypeEnum(String description) {
        this.description = description;
    }

}
