package com.mincho.herb.domain.user.entity;


import com.mincho.herb.domain.user.domain.Profile;
import com.mincho.herb.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "Member")
@Table(name = "member")
@Data
public class UserEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column
    private String role="ROLE_USER";


    @OneToOne(mappedBy = "member")
    private ProfileEntity profile;

    public static UserEntity toEntity(User userDomain){
        UserEntity userEntity = new UserEntity();
        userEntity.id = userDomain.getId();
        userEntity.email = userDomain.getEmail();
        userEntity.password = userDomain.getPassword();
        userEntity.role = userDomain.getRole();
        return userEntity;
    }

    public User toModel(){
        return User.builder()
                .id(this.id)
                .email(this.email)
                .password(this.password)
                .role(this.role)
                .build();

    }
}
