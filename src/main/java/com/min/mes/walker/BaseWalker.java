package com.min.mes.walker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.min.mes.dto.user.UserVO;
import com.min.mes.util.JwtUtil;
import com.min.mes.util.StringUtil;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Component
public abstract class BaseWalker {
/*
    protected final boolean IS_REAL;
    protected final List<String> ALLOW_DOMAIN_IP;

    @Value("${app.allow-domain-ip}")
    private List<String> allowDomainIp;

    @Value("${app.is-real}")
    private boolean isReal;
*/

    // 자식 클래스들이 사용할 수 있도록 protected로 선언
    // 실제 로거 객체 (자식 클래스 이름으로 초기화됨)
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected final String commonFailCd = "4000";
    protected final String commmonSuccessCd = "0000";


    // 사용자 ID 추출
    protected final String getLoginId() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName)
                .orElse(StringUtil.checkNull(null)); // == orElse("");

        /*
        // SecurityContext에서 인증 정보(userId) 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "";
        */
    }

    // 사용자 정보 전부 추출 - 근데 이건, 개인정보 null인 SessionUser 등 하나 만들어서 사용하는게 나을 듯
    protected final UserVO getLoginUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth != null && auth.getPrincipal() instanceof UserVO) {
            return (UserVO) auth.getPrincipal();
        }
        return null;
    }

    protected static void BaseWalker() {
        /*
        this.ALLOW_DOMAIN_IP = allowDomainIp;
        this.IS_REAL = isReal;

        String profile = System.getProperty("spring.profiles.active", "dev");
        System.out.println("######### [profile : " + profile +"] #########");

        if("prod".equals(profile)){

        } else {

        }

         */

    }

    protected void Sysout(Object text) {
        System.out.println("############# [ " + text + " ] #############");
    }


    // 현재 HTTP SESSION 반환
    // 없으면 새로 생성
    protected HttpSession getSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(true); // true면 없어서 새로 생성
    }




    /* Object to Map [S] */

    @Autowired
    private ObjectMapper objectMapper; // JSON to Map


    /**
     * JPA Entity나 객체를 Map으로 변환 (Optional 대응)
     */
    protected Map<String, Object> toMap(Object obj) {
        if (obj == null) return Collections.emptyMap();

        // Optional인 경우 안의 내용물 추출
        if (obj instanceof Optional) {
            Optional<?> opt = (Optional<?>) obj;
            return opt.map(this::convert).orElse(Collections.emptyMap());
        }

        return convert(obj);
    }

    private Map<String, Object> convert(Object obj) {
        try {
            return objectMapper.convertValue(obj, Map.class);
        } catch (Exception e) {
            logErr("[Walker] Map 변환 중 오류 발생: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * MyBatis의 Map 결과가 Null일 때 빈 Map을 반환하여 NullPointerException 방지
     */
    protected Map<String, Object> checkNullMap(Map<String, Object> paramMap) {
        return paramMap != null ? paramMap : Collections.emptyMap();
    }

    /* Object to Map [E] */


    /* Logging [S] */


    /**
     * 커스텀 info 로깅
     * @param obj 로그로 남길 메시지
     */
    protected void logInfo(Object obj) {
        // 1. 메시지 전처리 (예: 공백 제거, 특정 단어 필터링 등)
        String processedMsg = "[Walker] " + obj;

        // 2. 부가 기능 (예: 특정 조건일 때 알림 발송 등)

        // 3. 최종 출력 (설정한 쓰레드/타임 포맷에 맞춰 출력됨)
        logger.info(processedMsg);
    }

    protected void logErr(Object obj) {
        // 에러 발생 시 쓰레드 이름 등을 메시지에 강제로 포함할 수도 있음
        String threadName = Thread.currentThread().getName();
        logger.error("Thread: {} | data: {}", threadName, obj);
    }

    protected void logErr(Object obj, Object obj2) {
        // 에러 발생 시 쓰레드 이름 등을 메시지에 강제로 포함할 수도 있음
        String threadName = Thread.currentThread().getName();
        logger.error("Thread: {} | data: {}", threadName, obj + " || " + obj2);
    }

    protected void error(String message, Throwable t) {
        logger.error(message, t);
    }

    /* Logging [E] */
}
