package com.mincho.herb.domain.like.domain;

import com.mincho.herb.domain.herb.domain.Herb;
import com.mincho.herb.domain.user.domain.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HerbLike {
    private Long id;
    private Herb herb;
    private User user;
}
