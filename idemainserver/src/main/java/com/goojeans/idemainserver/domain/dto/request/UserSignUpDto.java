package com.goojeans.idemainserver.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserSignUpDto {

    private String email;
    private String password;
    private String nickname;
    private String bio;
    private String city;
    private Boolean terms;
}