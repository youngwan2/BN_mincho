package com.mincho.herb.domain.user.entity;


import com.mincho.herb.domain.user.domain.Profile;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity(name = "Profile")
@Table(name = "profile")
@Data
@ToString
public class ProfileEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nickname;

    @Column
    private String introduction;

    @Column
    private String avatarUrl;

    @OneToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;



    public static ProfileEntity toEntity(Profile profileDomain, MemberEntity member){
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.id = profileEntity.getId();
        profileEntity.nickname = profileDomain.getNickname();
        profileEntity.introduction = profileDomain.getIntroduction();
        profileEntity.avatarUrl= profileDomain.getAvatarUrl();
        profileEntity.member = member;
        return profileEntity;
    }

    public Profile toModel(){
        return Profile.builder()
                .nickname(this.nickname)
                .introduction(this.introduction)
                .avatarUrl(this.avatarUrl)
                .build();
    }
}
