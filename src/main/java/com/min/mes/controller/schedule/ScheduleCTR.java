package com.min.mes.controller.schedule;

import com.min.mes.ApiResponse;
import com.min.mes.common.exception.ErrorCode;
import com.min.mes.common.exception.GlobalException;
import com.min.mes.entity.UserDtlEntity;
import com.min.mes.entity.schedule.UserWorkScheduleEntity;
import com.min.mes.repository.jpa.UserDtlRepository;
import com.min.mes.repository.jpa.schedule.UserWorkScheduleRepository;
import com.min.mes.service.user.UserService;
import com.min.mes.util.StringUtil;
import com.min.mes.walker.BaseWalker;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleCTR extends BaseWalker {

    private final UserDtlRepository userDtlRepository;
    private final UserWorkScheduleRepository userWorkScheduleRepository;
    private final UserService userService;

    @PostMapping("check-status")
    public ResponseEntity<ApiResponse> existsByPersonalId (@AuthenticationPrincipal String userId, @RequestBody(required = false) Map<String, Object> paramMap) { // 파라미터 없음
        Boolean isExist = false;

        try {
            //String userId = StringUtil.checkNull(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            //isExist = userDtlRepository.existsByJu1(userId);
            isExist = userDtlRepository.isJuNotEmpty(userId);

        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        Map.of(
                                "isExist", isExist
                        )
                ));
    }


    @PostMapping("available-dates")
    public ResponseEntity<ApiResponse> getAvailableDates(@RequestBody Map<String, Object> paramMap) {
        Map<String, Object> dataMap = new HashMap<>();

        try {
            String userId = StringUtil.checkNull(SecurityContextHolder.getContext().getAuthentication().getPrincipal());

            // 1. 현재 날짜 기준으로 이번 달/다음 달 신청 내역 조회 (필요에 따라 기간 조절)
            // 예: 현재부터 30일치 데이터
            LocalDateTime start = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime end = start.plusDays(30);

            // 2. 해당 유저가 이미 신청한 날짜 리스트 가져오기
            List<UserWorkScheduleEntity> mySchedules = userWorkScheduleRepository.findByUserIdAndApntDtBetween(userId, start, end);

            // 3. (비즈니스 로직) 전체 인원 마감 여부 등은 별도 테이블이 필요하겠지만,
            // 현재는 본인이 신청한 날짜 리스트를 먼저 내려줍니다.
            List<LocalDateTime> appliedDates = mySchedules.stream()
                    .map(UserWorkScheduleEntity::getApntDt)
                    .toList();

            dataMap.put("appliedDates", appliedDates);
            // 만약 특정 날짜가 마감되었다는 정보가 있다면 여기에 추가 (ex: closedDates)

        } catch (Exception e) {
            logErr("Error fetching available dates", e);
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(dataMap));
    }

    @PostMapping("apply")
    @Transactional
    public ResponseEntity<ApiResponse> scheduleApply (@RequestBody Map<String, Object> paramMap) {
        Map<String, Object> dataMap = new HashMap<>();

        try {
            String userId = StringUtil.checkNull(SecurityContextHolder.getContext().getAuthentication().getPrincipal());



            if("".equals(userId)){

            } else {

                // 1. UserDetail 수정
                UserDtlEntity userDtlEntity = userDtlRepository.findById(userId).orElseThrow();
                if(userDtlEntity.getJu1() == null) {
                    paramMap.put("userId", userId);
                    userService.updateUserDtl(paramMap);
                }

                // 2. 스케쥴 추가
                // 날짜 리스트 처리 (dates=[2026-05-13])
                List<String> dateList = (List<String>) paramMap.get("dates");
                String brand = (String) paramMap.get("brand");
                String shuttleRegion = (String) paramMap.get("shuttleRegion");
                String shuttleStop = (String) paramMap.get("shuttleStop");

                for (String dateStr : dateList) {
                    // String -> LocalDateTime 변환 (시간 00:00:00 세팅)
                    LocalDateTime apntDt = LocalDateTime.parse(dateStr + "T00:00:00");

                    // 2-1. 중복 체크 (Double Check)
                    if (userWorkScheduleRepository.existsByUserIdAndApntDt(userId, apntDt)) {
                        logInfo("이미 신청한 날짜 : " + dateStr);

                        continue; // 혹은 throws
                    }

                    // 2-2. Entity 생성 및 저장
                    UserWorkScheduleEntity schedule = UserWorkScheduleEntity.builder()
                            .userId(userId)
                            .apntDt(apntDt)
                            .groupCd("mk_jw") // 고정값 혹은 설정값
                            .brandCd(brand)
                            .sttlArea(shuttleRegion)
                            .sttlLctn(shuttleStop)
                            .chkYn("N")
                            .workCnfmYn("N")
                            .workYn("N")
                            .build();

                    userWorkScheduleRepository.save(schedule);
                }

                dataMap.put("result", "success");
            }
        } catch (Exception e) {
            logErr("Apply Error", e);
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(dataMap));
    }

}
