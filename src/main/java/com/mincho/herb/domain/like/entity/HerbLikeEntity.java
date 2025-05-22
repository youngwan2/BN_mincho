package com.mincho.herb.domain.like.entity;

import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.like.domain.HerbLike;
import com.mincho.herb.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "HerbLike", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "herb_id"})
})
@Data
public class HerbLikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "herb_id")
    private HerbEntity herb;

    public static HerbLikeEntity toEntity(HerbLike herbLike){
        HerbLikeEntity herbLikeEntity = new HerbLikeEntity();
        herbLikeEntity.id = herbLike.getId();
        herbLikeEntity.user = UserEntity.toEntity(herbLike.getUser());
        herbLikeEntity.herb = HerbEntity.toEntity(herbLike.getHerb());

        return herbLikeEntity;
    }

    public HerbLike toModel(){
        return HerbLike.builder()
                .id(this.id)
                .user(this.user.toModel())
                .herb(this.herb.toModel())
                .build();
    }
}
