package com.goojeans.idemainserver.service;

import com.goojeans.idemainserver.domain.dto.request.UserSignUpDto;
import com.goojeans.idemainserver.domain.dto.response.UserInfoDto;
import com.goojeans.idemainserver.domain.entity.User;
import com.goojeans.idemainserver.repository.UserRepository;
import com.goojeans.idemainserver.util.Role;
import com.goojeans.idemainserver.util.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public void signUp(UserSignUpDto userSignUpDto) throws Exception {

        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new Exception("이미 존재하는 이메일입니다.");
        }

        if (userRepository.findByNickname(userSignUpDto.getNickname()).isPresent()) {
            throw new Exception("이미 존재하는 닉네임입니다.");
        }

        User user = User.builder()
                .email(userSignUpDto.getEmail())
                .password(userSignUpDto.getPassword())
                .nickname(userSignUpDto.getNickname())
                .bio(userSignUpDto.getBio())
                .city(userSignUpDto.getCity())
                .IsAdmin(Role.USER)
                .build();

        user.passwordEncode(passwordEncoder);
        userRepository.save(user);
    }



    /*
    private String email;
    private String nickname;
    private String imageUrl;
    private String Bio;
    private String city;
    private String isAdmin;
    private String socialId;
    private String AccessToken;
     */



    // 원래 UserInfoDto 로 리턴해야하지만 일단 테스트가 우선
    public UserInfoDto getUserInfo(HttpServletRequest request){

        UserInfoDto userInfoDto = new UserInfoDto();
        Optional<String> s = jwtService.extractAccessToken(request);
        String Token = s.orElse("not valid value");

        Optional<String> emailFromToken = jwtService.extractEmail(Token);
        if(emailFromToken.isPresent()){
            String e = emailFromToken.get().toString();
            Optional<User> user = userRepository.findByEmail(e);
            if(user.isPresent()){
                User u = user.get();
                userInfoDto.setEmail(u.getEmail());
                userInfoDto.setNickname(u.getNickname());
                userInfoDto.setImageUrl(u.getImageUrl());
                userInfoDto.setBio(u.getBio());
                userInfoDto.setCity(u.getCity());
                userInfoDto.setIsAdmin(u.getIsAdmin());
                userInfoDto.setSocialId(u.getSocialId());
                userInfoDto.setAccessToken(Token);
            }
        }
        return userInfoDto;
    }

    public void deleteMemberById(Long id) {
        userRepository.deleteById(id); // 이메일을 기준으로 삭제
    }



}
