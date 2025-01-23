package com.mincho.herb.domain.user.entity;


import com.mincho.herb.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "Member")
@Data
public class UserEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column
    private String nickname;

    @Column
    private String password;

    @Column
    private String role="ROLE_USER";

    public static UserEntity toEntity(User userDomain){
        UserEntity userEntity = new UserEntity();
        userEntity.id = userDomain.getId();
        userEntity.email = userDomain.getEmail();
        userEntity.nickname = userDomain.getNickname();
        userEntity.password = userDomain.getPassword();
        return userEntity;
    }

    public User toModel(){
        return User.builder()
                .id(this.id)
                .email(this.email)
                .password(this.password)
                .nickname(this.nickname)
                .build();

    }
}
