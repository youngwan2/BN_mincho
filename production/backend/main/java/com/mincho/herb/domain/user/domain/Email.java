package com.mincho.herb.domain.user.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class Email {

    public void sendEmail(){
        System.out.println("이메일전송");
    }
}
