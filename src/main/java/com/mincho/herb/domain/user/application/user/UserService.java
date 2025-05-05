package com.mincho.herb.domain.user.application.user;

import com.mincho.herb.domain.user.domain.Member;
import com.mincho.herb.domain.user.dto.DuplicateCheckDTO;
import com.mincho.herb.domain.user.dto.LoginRequestDTO;
import com.mincho.herb.domain.user.dto.RegisterRequestDTO;

import java.util.Map;

public interface UserService {

    Member register(RegisterRequestDTO registerRequestDTO);
    boolean dueCheck(DuplicateCheckDTO duplicateCheckDTO);
    boolean checkPassword(String email, String rawPassword);
    Map<String, String> login(LoginRequestDTO loginRequestDTO);
    void deleteUser(String email); // 회원탈퇴
    void updatePassword( String email, String password);
    Member findUserByEmail(String email);
    void logoutAll(Long id);

    boolean isLogin();

}
