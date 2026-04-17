package com.min.mes.controller.cloud.check;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckCTR {

    @GetMapping("/api/cloud/check")
    public String healthChk(){
        return "Health Chk : Alive";
    }
}
