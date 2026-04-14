package com.min.mes.controller.auth;

import com.min.mes.ApiResponse;
import com.min.mes.AppProperties;
import com.min.mes.entity.UserEntity;
import com.min.mes.repository.UserRepository;
import com.min.mes.service.LoginSVC;
import com.min.mes.service.user.UserService;
import com.min.mes.util.JwtUtil;
import com.min.mes.util.StringUtil;
import com.min.mes.walker.BaseWalker;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController // front로 JsonData 떨굼
@RequestMapping("/api/auth")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:5173") // 리액트 포트 허용
//@CrossOrigin(origins = BaseWalker.ALLOW_DOMAIN_IP) // 리액트 포트 허용
public class LoginCTR extends BaseWalker {
    private final LoginSVC loginService;
    private final UserService userService;
    private final AppProperties appProperties;
    private final JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody Map<String, Object> paramMap, HttpServletResponse response){
        logInfo(paramMap);

        Map returnMap = new HashMap();
        ResponseCookie accessCookie = null;
        ResponseCookie refreshCookie = null;

        String userId = StringUtil.checkNull(paramMap.get("userId"));
        String chkPass = StringUtil.checkNull(paramMap.get("password"));

        try {
            //loginService.loginProc(userId, chkPass);
            //ApiResponse apiResponse = (ApiResponse)loginService.loginProc(userId, chkPass);
            // ( 서비스 > 유저 인증 )
            String accessToken = "";
            String refreshToken = "";

            Map resultMap = loginService.loginProc(userId, chkPass);
            Sysout(resultMap);

            if("0000".equals(resultMap.get("cd"))) {
                // ( 유저 세팅 :: UserEntity > UserVO )
                UserEntity user = (UserEntity) resultMap.get("user");

                // ( 토큰 생성 )
                accessToken = jwtUtil.generateToken(user.getUserId(), user.getUserNm());
                refreshToken = jwtUtil.generateToken(user.getUserId(), user.getUserNm());

                // ( refreshToken DB에 저장 - 추후 대조 )
                userService.updateRefreshToken(user.getUserId(), refreshToken);

                // ( 쿠키 굽기 )
                accessCookie = jwtUtil.createCookie("accessToken", accessToken);
                refreshCookie = jwtUtil.createCookie("refreshToken", refreshToken);

                returnMap.put("userId", user.getUserId());
                returnMap.put("userNm", user.getUserNm());


            }

            // ( 바디에는 토큰 제외 정보만 )
            return ResponseEntity.status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    //.body(ApiResponse.success(apiResponse));
                    .body(ApiResponse.success(
                            Map.of(
                                    "user", returnMap
                            )
                    ));

            //return ResponseEntity.ok(new ApiResponse<>(200, "로그인 성공...", resMap));
            //return new ApiResponse<>(200, "성공이요", null);

        } catch (Exception e) {
            logErr(e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    //.body(new ApiResponse<>("404", e.getMessage(), null));
                    .body(ApiResponse.failMsg("로그인 오류"));
            //return ResponseEntity.internalServerError().body("에러 발생: " + e.getMessage());
        }


    }


/*
    @GetMapping("/profile")
    public Map<String,Object> profile(@RequestHeader("Authorization") String authHeader) {
        try {
            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("토큰 없음");
            }
            String token = authHeader.substring(7);
            String username = JwtUtil.validateToken(token);
            return Map.of("user", Map.of("name", username));
        } catch (Exception e) {
            throw new RuntimeException("인증 실패");
        }
    }
*/


    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response){
        // 1. 쿠키에서 RefreshToken 추출
        String refreshToken = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(c -> "refreshToken".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                //.orElseThrow(() -> new RuntimeException("Refresh Token이 없습니다."));
                .orElse(null);

        // 없는 경우..
        if(refreshToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token이 없습니다.");
        }

        try {
            // 2. 토큰 검증 및 유저 ID 추출
            String userId = JwtUtil.validateToken(refreshToken);

            // 3. DB에 저장된 토큰과 일치하는지 확인
            UserEntity user = userService.getUser(userId);

            // 3-1 DB에 없거나, 브라우저에서 보낸 것과 다르면 "부정 접근"으로 간주
            if(user == null || !refreshToken.equals(user.getChkToken())){
                jwtUtil.deleteCookie(response, "refreshToken");
                jwtUtil.deleteCookie(response, "accessToken");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 세션입니다. 다시 로그인 해주세요.");
            }

            // 4. 새로운 AccessToken 생성 및 쿠키 설정 (Refresh는 그대로 써도 되고, 같이 갱신해도 됨..)
            String newAccessToken = jwtUtil.generateToken(userId, user.getUserNm());
            ResponseCookie newAccessCookie = jwtUtil.createCookie("accessToken", newAccessToken);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, newAccessCookie.toString())
                    .body(ApiResponse.success("토큰 재발급 완료"));


        } catch(ExpiredJwtException e ){

            // Refresh Token마저 만료된 경우 DB와 쿠키 모두 정리
            // [중요] 여기서 DB의 토큰을 null로 날려버립니다.
            // 쿠키에서 토큰은 없지만, 어떤 유저의 토큰이었는지 알 수 없으므로
            // 보통은 로그인 시점에 덮어쓰거나, 로그아웃 시점에 지우는 게 일반적입니다.

            // 만약 refreshToken이 null이 아니었지만 만료된 거라면,
            // 만료된 토큰에서도 클레임(Claims)은 강제로 추출할 수 있습니다.
            String expiredUserId = jwtUtil.getUserIdFromExpiredToken(refreshToken);

            if(expiredUserId != null) {
                userService.updateRefreshToken(expiredUserId, null); // DB 청소
            }

            jwtUtil.deleteCookie(response, "refreshToken");
            jwtUtil.deleteCookie(response, "accessToken");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("세션이 만료되었습니다.");
        }

    /*as-is
        // 4. 새로운 AccessToken 생성 (Refresh는 그대로 써도 되고, 같이 갱신해도 됨..)
        String newAceessToken = jwtUtil.generateToken(userId, user.getUserNm());
        ResponseCookie newAccessCookie = jwtUtil.createCookie("accessToken", newAceessToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newAccessCookie.toString())
                .body(ApiResponse.success("토큰 재발급 완료"));

     */
    }

    //@PreAuthorize("isAutheniticated()")
    //@SecurityConfig
    @GetMapping("/profile")
    public ResponseEntity<Object> profile() throws Exception {
        Map returnMap = new HashMap();

        UserEntity user = null;

        // JWT 검증 실행. 검증 실패 시 에러 뱉고 이하 로직 수행없이 종료
        String userId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!"".equals(userId)){
            user = userService.getUser(userId);
            returnMap.put("userId", user.getUserId());
            returnMap.put("userNm", user.getUserNm());
        }



        // ( 바디에는 토큰 제외 정보만 )
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        Map.of(
                                "user", returnMap
                        )
                ));




        /*
        return ResponseEntity.status(HttpStatus.OK)
                //.header(HttpHeaders.SET_COOKIE, JWTCookie.toString())
                .body(ApiResponse.success(user.getUserId()));

        */


        //return Map.of("user", Map.of("name", userId));


        //return Map.of("user", resMap);
        /*
        return new ApiResponse<>(
                "0000",
                "로그인 성공",
                Map.of(
                        "cd", "402",
                        "msg", "아이디와 비밀번호를 확인해주세요.",
                        "user", Map.of(
                                "id", resMap.get("userId"),
                                "name", resMap.get("userName")
                        ),
                        "token", ""
                )
        );

         */

    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response){
        // 1. 로그인된 유저 ID 가져오기 (SecurityContextHolder 등 활용가능)
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        logInfo("userId :::: " + userId);

        // 2. DB에서 CHK_TOKEN 삭제
        userService.updateRefreshToken(userId, null);

        /* as-is
        // 3. 쿠키 만료(Max-Age : 0)
        ResponseCookie deleteAccess = ResponseCookie.from("accessToken", "")
                .maxAge(0).path("/").build();
        ResponseCookie deleteRefresh = ResponseCookie.from("refreshToken", "")
                .maxAge(0).path("/").build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteAccess.toString())
                .header(HttpHeaders.SET_COOKIE, deleteRefresh.toString())
                .body(ApiResponse.success("로그아웃 되었습니다."));
        */

        /* TO-BE */
        jwtUtil.deleteCookie(response,"accessToken");
        jwtUtil.deleteCookie(response,"refreshToken");

        return ResponseEntity.ok()
                .body(ApiResponse.success("로그아웃 되었습니다."));

    }



    // 추후 로그아웃 메소드 내 아래내용 필수
    /*

    ResponseCookie cookie = ResponseCookie.from("accessToken", "")
    .path("/")
    .httpOnly(true)
    .maxAge(0) // 즉시 만료시켜서 브라우저에서 지우게 함
    .build();
response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
     */


}
