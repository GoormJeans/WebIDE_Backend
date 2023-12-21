package com.goojeans.idemainserver.controller;


import com.goojeans.idemainserver.domain.dto.request.UserSignUpDto;
import com.goojeans.idemainserver.domain.dto.response.ResponseDto;
import com.goojeans.idemainserver.domain.dto.response.UserInfoDto;
import com.goojeans.idemainserver.repository.UserRepository;
import com.goojeans.idemainserver.service.UserService;
import com.goojeans.idemainserver.util.jwt.service.JwtService;
import com.goojeans.idemainserver.util.oauth2.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserRepository userRepository;
    private final JwtService jwtService;



    // 억지로...만들어줌
    @GetMapping("/")
    public void home(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<String> s = jwtService.extractAccessToken(request);
        String Token = s.orElse("not valid value");

        boolean tokenValid = jwtService.isTokenValid(Token);
        if(tokenValid){
            response.sendRedirect("/main");
            return;
        }

        response.sendRedirect("/login");

    }


    @PostMapping("/sign-up")
    public String signUp(@RequestBody UserSignUpDto userSignUpDto) throws Exception {
        userService.signUp(userSignUpDto);

        return "회원가입 성공";
    }

    @GetMapping("/main")
    public String main(){
        return "login success then main";
    }

    @GetMapping("/oauth/sign-up")
    public String oauthSignup(@RequestParam(name="token")String token,HttpServletResponse response) throws IOException {
        log.info("Token received:{}",token);
        //response.setHeader("token",token);
        //response.sendRedirect("/authenticatedOnly");
        return jwtService.extractEmail(token).get().toString();
    }


    // 필터 확인 겸 인증이 안된 사람은 접근 불가
    @GetMapping("/authenticatedOnly")
    public String authenticatedOnly(HttpServletRequest request,HttpServletResponse response){
        return "you are authenticated";
    }

    @PostMapping("/userInfo")
    public ResponseDto<UserInfoDto> userInfo(HttpServletRequest request, HttpServletResponse response){
        ResponseDto<UserInfoDto> responseDto = new ResponseDto<>();
        Optional<String> s = jwtService.extractAccessToken(request);
        String Token = s.orElse("not valid value");

        boolean tokenValid = jwtService.isTokenValid(Token);
        System.out.println(tokenValid);

        UserInfoDto userInfo = userService.getUserInfo(request);
        responseDto.setStatusCode(2000);
        List<UserInfoDto> list = new ArrayList<>();
        list.add(userInfo);

        responseDto.setData(list);



//==============================================================
        // JwtService 의존성 주입 필요
        // 파라미터에 "HttpServletRequest request"  추가 필요

        // 헤더에 담긴 토큰 추출
        Optional<String> jwttoken = jwtService.extractAccessToken(request);
        String token = jwttoken.orElse("not valid value");
        // 토큰 복호화
        Map<String, String> decode = jwtService.decode(token);
        // 닉네임
        String nickname = decode.get("nickname");
        // id
        String id = decode.get("id");

        log.info("======> nickname={},id={}",nickname,id);
//==============================================================




        return  responseDto;
    }






    @GetMapping("/PUBLIC")
    public String pb(){
        return "public page";
    }


}
