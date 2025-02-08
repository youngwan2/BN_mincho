# Mincho




## Memo
### Entity 구조 예시
- 아래 구조는 현 프로젝트에서 Entity 를 구성할 때 기본이 되는 틀의 예시 입니다.
```java
package com.mincho.herb.member.entity;


import domain.com.mincho.herb.domain.member.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserEntity {
    // 필드
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column
    private String nickname;

    @Column
    private String password;
   
    // Domain -> Entity
    public static UserEntity toEntity(User memberDomain){
        UserEntity memberEntity = new UserEntity();
        memberEntity.id = memberDomain.getId();
        memberEntity.email = memberDomain.getEmail();
        memberEntity.nickname = memberDomain.getNickname();
        return memberEntity;
    }
    // Entity -> Domain
    public User toModel(){
        return User.builder()
                .id(this.id)
                .email(this.email)
                .password(this.password)
                .nickname(this.nickname)
                .build();

    }
}

```