package com.mincho.herb.domain.user.entity;

public enum UserStatusEnum {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    DELETED("탈퇴");

    private final String description;

    UserStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
