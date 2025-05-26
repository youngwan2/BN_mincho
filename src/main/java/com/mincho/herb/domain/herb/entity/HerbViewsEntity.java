package com.mincho.herb.domain.herb.entity;

import com.mincho.herb.domain.herb.domain.HerbViews;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "herbViews" )
public class HerbViewsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long viewCount;

    @OneToOne
    @JoinColumn(name = "herb_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private HerbEntity herb;


    public static HerbViewsEntity toEntity(HerbViews herbViews, HerbEntity herbEntity){
            HerbViewsEntity herbViewsEntity = new HerbViewsEntity();
            herbViewsEntity.setId(herbViews.getId());
            herbViewsEntity.setHerb(herbEntity);
            herbViewsEntity.setViewCount(herbViews.getViewCount());

            return herbViewsEntity;
    }

    public HerbViews toModel(){
        return HerbViews.builder()
                .id(this.id)
                .viewCount(this.viewCount)
                .herb(this.herb.toModel())
                .build();
    }
}
