package com.mincho.herb.domain.user.application.user;

import com.mincho.herb.domain.user.domain.Member;
import com.mincho.herb.domain.user.dto.DuplicateCheckDTO;
import com.mincho.herb.domain.user.dto.RequestLoginDTO;
import com.mincho.herb.domain.user.dto.RequestRegisterDTO;

import java.util.Map;

public interface UserService {

    Member register(RequestRegisterDTO requestRegisterDTO);
    boolean dueCheck(DuplicateCheckDTO duplicateCheckDTO);
    Map<String, String> login(RequestLoginDTO requestLoginDTO);
    void deleteUser(String email); // 회원탈퇴
    void updatePassword(String password, String email);
    Member findUserByEmail(String email);
    void logout(String refreshToken);
    void logoutAll(Long id);
    
}
