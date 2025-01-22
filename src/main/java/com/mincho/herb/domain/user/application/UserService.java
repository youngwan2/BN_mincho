package com.mincho.herb.domain.user.application;

import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.dto.DuplicateCheckDTO;
import com.mincho.herb.domain.user.dto.RequestRegisterDTO;

public interface UserService {

    void register(RequestRegisterDTO requestRegisterDTO);

    boolean dueCheck(DuplicateCheckDTO duplicateCheckDTO);
}
