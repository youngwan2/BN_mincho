package com.mincho.herb.domain.favorite.entity;

import com.mincho.herb.common.base.BaseEntity;
import com.mincho.herb.domain.favorite.domain.FavoriteHerb;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.user.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "FavoriteHerb", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "herbSummary_id"})
})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavoriteHerbEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "herb_id")
    private HerbEntity herb;
    private String url;


    public static FavoriteHerbEntity toEntity(FavoriteHerb favoriteHerb){
        FavoriteHerbEntity favoriteHerbEntity = new FavoriteHerbEntity();
        favoriteHerbEntity.id = favoriteHerb.getId();
        favoriteHerbEntity.member = MemberEntity.toEntity(favoriteHerb.getMember());
        favoriteHerbEntity.herb = HerbEntity.toEntity(favoriteHerb.getHerb());
        favoriteHerbEntity.url = favoriteHerb.getUrl();

        return favoriteHerbEntity;
    }

    public FavoriteHerb toModel(){
        return FavoriteHerb.builder()
                .id(this.id)
                .member(this.member.toModel())
                .herb(this.herb.toModel())
                .url(this.url)
                .build();
    }
}
