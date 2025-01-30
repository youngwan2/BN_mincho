package com.mincho.herb.domain.favorite.entity;

import com.mincho.herb.common.base.BaseEntity;
import com.mincho.herb.domain.favorite.domain.FavoriteHerb;
import com.mincho.herb.domain.herb.entity.HerbSummaryEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
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
    private UserEntity member;

    @ManyToOne
    @JoinColumn(name = "herbSummary_id")
    private HerbSummaryEntity herbSummary;
    private String url;


    public static FavoriteHerbEntity toEntity(FavoriteHerb favoriteHerb){
        FavoriteHerbEntity favoriteHerbEntity = new FavoriteHerbEntity();
        favoriteHerbEntity.id = favoriteHerb.getId();
        favoriteHerbEntity.member = UserEntity.toEntity(favoriteHerb.getUser());
        favoriteHerbEntity.herbSummary = HerbSummaryEntity.toEntity(favoriteHerb.getHerbSummary());
        favoriteHerbEntity.url = favoriteHerb.getUrl();

        return favoriteHerbEntity;
    }

    public FavoriteHerb toModel(){
        return FavoriteHerb.builder()
                .id(this.id)
                .user(this.member.toModel())
                .herbSummary(this.herbSummary.toModel())
                .url(this.url)
                .build();
    }
}
