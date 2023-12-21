package com.goojeans.idemainserver.util.oauth2.handler;

import com.goojeans.idemainserver.domain.entity.User;
import com.goojeans.idemainserver.repository.UserRepository;
import com.goojeans.idemainserver.util.Role;
import com.goojeans.idemainserver.util.jwt.service.JwtService;
import com.goojeans.idemainserver.util.oauth2.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
//@Transactional
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");

        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            // user Id, user nickname

            Optional<User> user  =userRepository.findByEmail(oAuth2User.getEmail());
            Long id = user.get().getId();
            String nickname = user.get().getNickname();

            // User의 Role이 GUEST일 경우 처음 요청한 회원이므로 회원가입 페이지로 리다이렉트
            if(oAuth2User.getRole() == Role.GUEST) {
                String accessToken = jwtService.createAccessToken(oAuth2User.getEmail(),id,nickname);

                log.info("accessToken={}",accessToken);

                jwtService.sendAccessAndRefreshToken(response, accessToken, null);

                // 🌟🌟🌟클라이언트에게 리다이렉트 경로에 query param 으로 토큰 리턴해주기 🌟🌟🌟
                // 로그인 이후 리다이렉트할 URL 을 생성
                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("/oauth/sign-up");

                // 인증정보 얻기 -> 근데 여기선 accesstoken 바로 있으니까 굳이 필요없
                //Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();

                // 토큰 파라미터 추가
                builder.queryParam("token",accessToken);

                // 최종 URL로 리다이렉트
                response.sendRedirect(builder.toUriString());


                // 원래 있었음
                response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
                response.setHeader("Authorization",accessToken);

                /**
                 * 😡❌원래 리다이렉트 전에 헤더에 토큰 값을 넣어서 리다이렉트하려했지만, 리다이렉트페이지에서 헤더에 값 조회가 안됨
                 */
                //response.sendRedirect("/oauth/sign-up"); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트 "oauth2/sign-up"
                //String authorization = response.getHeader("Authorization");
                //log.info("Oauth2에서 헤더 확인:{}",authorization);


//                jwtService.sendAccessAndRefreshToken(response, accessToken, null);
//                User findUser = userRepository.findByEmail(oAuth2User.getEmail())
//                                .orElseThrow(() -> new IllegalArgumentException("이메일에 해당하는 유저가 없습니다."));
//                findUser.authorizeUser();

            } else {
                loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성
            }
        } catch (Exception e) {
            throw e;
        }

    }

    // TODO : 소셜 로그인 시에도 무조건 토큰 생성하지 말고 JWT 인증 필터처럼 RefreshToken 유/무에 따라 다르게 처리해보기
    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {

        Optional<User> user  =userRepository.findByEmail(oAuth2User.getEmail());

        Long id = user.get().getId();
        String nickname = user.get().getNickname();


        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail(),id,nickname);
        String refreshToken = jwtService.createRefreshToken();


        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);


        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
    }
}