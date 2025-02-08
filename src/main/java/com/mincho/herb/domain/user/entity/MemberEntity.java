package com.mincho.herb.domain.user.entity;

import com.mincho.herb.common.base.BaseEntity;
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

    @Column
    private String password;

    @Column
    private String role;

    @OneToOne(mappedBy = "member")
    private ProfileEntity profile;


    public static MemberEntity toEntity(Member memberDomain){
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.id = memberDomain.getId();
        memberEntity.email = memberDomain.getEmail();
        memberEntity.password = memberDomain.getPassword();
        memberEntity.role = memberDomain.getRole();
        return memberEntity;
    }

    public Member toModel(){
        return Member.builder()
                .id(this.id)
                .email(this.email)
                .password(this.password)
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
