package com.min.mes.controller.schedule;

import com.min.mes.ApiResponse;
import com.min.mes.repository.UserRepository;
import com.min.mes.util.GeoUtil;
import com.min.mes.util.StringUtil;
import com.min.mes.walker.BaseWalker;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController // front로 JsonData 떨굼
@RequestMapping("/api/checkIn")
@RequiredArgsConstructor
public class WorkCTR extends BaseWalker {

    // 회사 좌표
    // 일원역
    /*
    private static final double COMPANY_LAT = 37.4836;
    private static final double COMPANY_LNG = 127.0844;
    */
    // Jara
    private static final double COMPANY_LAT = 37.2326866;
    private static final double COMPANY_LNG = 127.3606248;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("workProc")
    public ResponseEntity<ApiResponse> workProc (@RequestBody Map<String, Double> paramMap, HttpServletResponse response){
        Map<String, Object> returnMap = new HashMap<>();

        // 1. 위치기반 거리 유효성 체크
        if("".equals(StringUtil.checkNull(paramMap.get("lat"))) || "".equals(StringUtil.checkNull(paramMap.get("lng")))) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.failMsg("잘못된 접근입니다."));
        }

        double userLat = paramMap.get("lat");
        double userLng = paramMap.get("lng");

        double distance = GeoUtil.distance(
                userLat, userLng,
                COMPANY_LAT, COMPANY_LNG
        );

        // 🔥 서버 최종 검증
        if (distance <= 300) {
            returnMap.put("success", true);
            returnMap.put("message", "출근 완료");

            // 👉 여기서 Supabase insert or update 하면 됨

        } else {
            returnMap.put("success", false);
            returnMap.put("message", "출근 위치가 아닙니다");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        Map.of(
                                "info", returnMap
                        )
                ));
    }


}
