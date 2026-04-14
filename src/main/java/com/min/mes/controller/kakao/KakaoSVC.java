package com.min.mes.controller.kakao;

import com.min.mes.walker.BaseWalker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class KakaoSVC extends BaseWalker {

    @Value("${kakao.client-id}") // application.yaml
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.client-secret}")
    private String clientSecret;

        /**
     * 1. 인가 코드로 카카오 토큰 받기
     */
    public String getKakaoAccessToken(String code) {
        String accessToken = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            RestTemplate rt = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", clientId);
            params.add("redirect_uri", redirectUri);
            params.add("code", code);
            params.add("client_secret", clientSecret);

            HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
            ResponseEntity<Map> response = rt.exchange(reqURL, HttpMethod.POST, kakaoTokenRequest, Map.class);

            accessToken = (String) response.getBody().get("access_token");
        } catch (Exception e) {
            log.error("카카오 토큰 발급 에러: " + e.getMessage());
        }

        return accessToken;
    }

    /**
     * 2. 액세스 토큰으로 카카오 사용자 정보 가져오기
     */
    public Map<String, Object> getKakaoUserInfo(String accessToken) {
        String reqURL = "https://kapi.kakao.com/v2/user/me";

        try {
            RestTemplate rt = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            // TO-BE
            HttpEntity<String> entity = new HttpEntity<>(headers); // GET 방식이므로 바디 없이 헤더만 설정
            ResponseEntity<Map> response = rt.exchange( // POST 대신 GET으로 호출
                    reqURL,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            logInfo(response);


            // AS-IS
            //HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);
            //ResponseEntity<Map> response = rt.exchange(reqURL, HttpMethod.POST, kakaoProfileRequest, Map.class);

            return response.getBody(); // id, properties, kakao_account 등이 담김
        } catch (Exception e) {
            log.error("카카오 사용자 정보 조회 에러: " + e.getMessage());
            return null;
        }
    }
}
