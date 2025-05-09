package com.mincho.herb.domain.user.entity;

import com.mincho.herb.global.base.BaseEntity;
import com.mincho.herb.domain.user.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Member")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;
    private String role;

    private String provider; // 리소스 서버 제공자
    private String providerId; // 리소스 서버 식별자

    @OneToOne(mappedBy = "member")
    private ProfileEntity profile;




    public static MemberEntity toEntity(Member memberDomain){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.id = memberDomain.getId();
        memberEntity.email = memberDomain.getEmail();
        memberEntity.password = memberDomain.getPassword();
        memberEntity.providerId = memberDomain.getProviderId();
        memberEntity.provider  =memberDomain.getProvider();
        memberEntity.role = memberDomain.getRole();
        return memberEntity;
    }

    public Member toModel(){
        return Member.builder()
                .id(this.id)
                .email(this.email)
                .password(this.password)
                .provider(this.provider)
                .providerId(this.providerId)
                .role(this.role)
                .build();

    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", profile=" + profile +
                '}';
    }
}
