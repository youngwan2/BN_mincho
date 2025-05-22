package com.mincho.herb.domain.user.application.user;

import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.dto.DuplicateCheckDTO;
import com.mincho.herb.domain.user.dto.LoginRequestDTO;
import com.mincho.herb.domain.user.dto.RegisterRequestDTO;
import com.mincho.herb.domain.user.entity.UserEntity;

import java.util.Map;

public interface UserService {

    User register(RegisterRequestDTO registerRequestDTO);
    boolean dueCheck(DuplicateCheckDTO duplicateCheckDTO);
    boolean checkPassword(String email, String rawPassword);
    Map<String, String> login(LoginRequestDTO loginRequestDTO);
    void deleteUser(String email); // 회원탈퇴
    void updatePassword( String email, String password);
    UserEntity getUserByEmail(String email);
    UserEntity getUserByEmailOrNull(String email);
    void logoutAll(Long id);

    boolean isLogin();

}
