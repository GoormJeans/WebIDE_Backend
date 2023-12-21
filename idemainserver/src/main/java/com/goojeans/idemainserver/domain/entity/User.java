package com.goojeans.idemainserver.domain.entity;

import com.goojeans.idemainserver.util.Role;
import com.goojeans.idemainserver.util.SocialType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;




@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@Table(name = "USERS")
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email; // 이메일
    private String password; // 비밀번호
    private String nickname; // 닉네임
    private String imageUrl; // 프로필 이미지
    //private int age;
    private String bio; // 블로그 주소
    private String city; // 사는 도시
    private boolean terms; // 약관 동의 여부

    @Enumerated(EnumType.STRING)
    private Role IsAdmin;

    @Enumerated(EnumType.STRING)
    private SocialType socialType; // KAKAO, NAVER, GOOGLE

    private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

    private String refreshToken; // 리프레시 토큰

    // 유저 권한 설정 메소드
    public void authorizeUser() {
        this.IsAdmin = Role.USER ;
    }

    // 비밀번호 암호화 메소드
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    //== 유저 필드 업데이트 ==//

    // 닉네임 변경 불가
//    public void updateNickname(String updateNickname) {
//        this.nickname = updateNickname;
//    }


    // 블로그주소업데이트
    public void updateBlog(String blog) {
        this.bio = blog;
    }


    // 주소업데이트
    public void updateCity(String updateCity) {
        this.city = updateCity;
    }


    // 비밀번호 업데이트
    public void updatePassword(String updatePassword, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(updatePassword);
    }

    // 리프레쉬 토큰 업데이트
    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }
}
