package com.mincho.herb.domain.user.application;

import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.dto.DuplicateCheckDTO;
import com.mincho.herb.domain.user.dto.RequestLoginDTO;
import com.mincho.herb.domain.user.dto.RequestRegisterDTO;

import java.util.Map;

public interface UserService {

    void register(RequestRegisterDTO requestRegisterDTO);
    boolean dueCheck(DuplicateCheckDTO duplicateCheckDTO);
    Map<String, String> login(RequestLoginDTO requestLoginDTO);
    void deleteUser(String email); // 회원탈퇴
    User findUserByEmail(String email);
    
}
