package com.goojeans.idemainserver.util.login.handler;

import com.goojeans.idemainserver.domain.entity.User;
import com.goojeans.idemainserver.repository.UserRepository;
import com.goojeans.idemainserver.util.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Value("${jwt.access.expiration}")
    private String accessTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // user Email (이메일)
        String email = extractUsername(authentication); // 인증 정보에서 Username(email) 추출

        // user GeneratedValue (기본키)
        Long Id = extractUserId(authentication);

        // user Nickname (닉네임)
        String Nickname = extractUserNickname(authentication);


        /**
         * 변경된 사항
         * jwt token 생성 시
         * user 의 정보( email, GeneratedValue, Nickname ) 을 넣는다.
         * 이후 서비스 db 접근을 최소화 하기 위함
         *
         */

        String accessToken = jwtService.createAccessToken(email,Id,Nickname); // JwtService의 createAccessToken을 사용하여 AccessToken 발급
        String refreshToken = jwtService.createRefreshToken(); // JwtService의 createRefreshToken을 사용하여 RefreshToken 발급

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken); // 응답 헤더에 AccessToken, RefreshToken 실어서 응답

        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    user.updateRefreshToken(refreshToken);
                    userRepository.saveAndFlush(user);
                });
        log.info("로그인에 성공하였습니다. 이메일 : {}", email);
        log.info("로그인에 성공하였습니다. AccessToken : {}", accessToken);
        log.info("발급된 AccessToken 만료 기간 : {}", accessTokenExpiration);
        response.setHeader("Athenthication",accessToken);
        response.sendRedirect("/main");

        // SecurityContextHolder 에 저장해야한다는데... 알아보기
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    // extractUsername 이지만 사실 userEmail 추출
    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }


    // userId(GeneratedValue) 추출
    private Long extractUserId(Authentication authentication){
        UserDetails whatsindata = (UserDetails) authentication.getPrincipal();
        String username = whatsindata.getUsername(); // getUsername() 이지만..이메일이나옴
        Optional<User> user = userRepository.findByEmail(username);
        return user.get().getId();
    }

    // userNickname 추출
    private String extractUserNickname(Authentication authentication){
        UserDetails whatsindata = (UserDetails) authentication.getPrincipal();
        String username = whatsindata.getUsername();
        Optional<User> user = userRepository.findByEmail(username);
        return user.get().getNickname();



    }

}