package com.mincho.herb.domain.herb.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
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

}
