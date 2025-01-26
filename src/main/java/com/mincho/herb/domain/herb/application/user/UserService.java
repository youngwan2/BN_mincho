package com.mincho.herb.domain.herb.application.user;

import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.dto.DuplicateCheckDTO;
import com.mincho.herb.domain.user.dto.RequestLoginDTO;
import com.mincho.herb.domain.user.dto.RequestRegisterDTO;

import java.util.Map;

public interface UserService {

    User register(RequestRegisterDTO requestRegisterDTO);
    boolean dueCheck(DuplicateCheckDTO duplicateCheckDTO);
    Map<String, String> login(RequestLoginDTO requestLoginDTO);
    void deleteUser(String email); // 회원탈퇴
    void updatePassword(String password, String email);
    User findUserByEmail(String email);
    void logout(String refreshToken);
    void logoutAll(Long id);
    
}
