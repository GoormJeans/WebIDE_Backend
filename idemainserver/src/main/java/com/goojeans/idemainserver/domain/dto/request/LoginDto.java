package com.goojeans.idemainserver.domain.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@NoArgsConstructor
@Getter
@Service
public class LoginDto {
    private String email;
    private String password;
}
