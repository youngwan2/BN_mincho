package com.mincho.herb.domain.bookmark.entity;

import com.mincho.herb.global.base.BaseEntity;
import com.mincho.herb.domain.bookmark.domain.HerbBookmark;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.user.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "HerbBookmark", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "herb_id"})
})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class HerbBookmarkEntity extends BaseEntity {
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


    public HerbBookmark toModel(){
        return HerbBookmark.builder()
                .id(this.id)
                .url(this.url)
                .build();
    }
}
