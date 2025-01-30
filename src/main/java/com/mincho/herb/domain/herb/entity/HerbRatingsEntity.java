package com.mincho.herb.domain.herb.entity;

import com.mincho.herb.common.base.BaseEntity;
import com.mincho.herb.domain.herb.domain.HerbRatings;
import com.mincho.herb.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "HerbRatings",
        uniqueConstraints = @UniqueConstraint(columnNames = {"herbSummary_id", "user_id"})
)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class HerbRatingsEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private UserEntity member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "herbSummary_id", nullable = false)
    private HerbSummaryEntity herbSummary;

    private Integer score;


    public static HerbRatingsEntity toEntity(HerbRatings herbRatings){
        HerbRatingsEntity herbRatingsEntity = new HerbRatingsEntity();

        herbRatingsEntity.id = herbRatings.getId();
        herbRatingsEntity.score = herbRatings.getScore();

        return herbRatingsEntity;
    }

    public HerbRatings toModel(){
        return HerbRatings.builder()
                .id(this.id)
                .score(this.score)
                .build();
    }
}
