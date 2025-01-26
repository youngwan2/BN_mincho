package com.mincho.herb.domain.user.entity;

import com.mincho.herb.common.base.BaseEntity;
import com.mincho.herb.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import java.time.LocalDateTime;

@Entity(name = "Member")
@Table(name = "member")
@Getter
@Setter
public class UserEntity extends BaseEntity {

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

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


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
