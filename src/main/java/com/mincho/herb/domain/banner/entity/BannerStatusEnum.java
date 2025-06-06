package com.mincho.herb.domain.banner.entity;

import lombok.Getter;

@Getter
public enum BannerStatusEnum {
        ACTIVE("활성"),
        INACTIVE("비활성"),
        SCHEDULED("예약"),
        EXPIRED("만료");

        private final String description;

        BannerStatusEnum(String description) {
            this.description = description;
        }

}
