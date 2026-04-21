package com.min.mes.service.sendSNS.brevo;

import com.min.mes.entity.UserEntity;
import com.min.mes.walker.BaseWalker;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sendinblue.ApiClient;
import sendinblue.Configuration;
import sendinblue.auth.ApiKeyAuth;
import sibApi.TransactionalEmailsApi;
import sibModel.SendSmtpEmail;
import sibModel.SendSmtpEmailSender;
import sibModel.SendSmtpEmailTo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class BrevoSVC extends BaseWalker {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.api.from-email}")
    private String brevoSenderEmail;

    @Value("${brevo.api.from-name}")
    private String brevoSenderName;

    private TransactionalEmailsApi apiInstance;

    @PostConstruct
    public void init() {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth apiKeyAuth = (ApiKeyAuth) defaultClient.getAuthentication("api-key");
        apiKeyAuth.setApiKey(brevoApiKey);
        apiInstance = new TransactionalEmailsApi();
    }


    public void snedEmail(Map infoMap, UserEntity user) {
        String subject = "";
        String htmlContent = "";


        if("id".equals(infoMap.get("type"))){
            subject = "요청하신 아이디 찾기 결과입니다.";
            htmlContent =
                    "<h3>안녕하세요. mes입니다.</h3>" +
                    "<p>요청하신 아이디 정보를 안내해 드립니다.</p>" +
                    "<p>아이디: <strong>" + user.getUserId() + "</strong></p>";
        } else if("pw".equals(infoMap.get("type"))){
            subject = "요청하신 비밀번호 찾기 결과입니다.";
            htmlContent =
                    "<h3>안녕하세요. mes입니다.</h3>" +
                    "<p>요청하신 비밀번호 정보를 안내해 드립니다.</p>" +
                    "<p>아래의 임시비밀번호로 귀하의 비밀번호를 설정하였으며</p>" +
                    "<p>임시비밀번호로 로그인 후 비밀번호를 변경하여 사용하시기 바랍니다.</p>" +
                    "<p>임시비밀번호: <strong>" + infoMap.get("tmpPw") + "</strong></p>";
        }

        SendSmtpEmail sendSmtpEmail = new SendSmtpEmail();

        // 보내는 사람 설정
        SendSmtpEmailSender sender = new SendSmtpEmailSender();
        sender.setEmail(brevoSenderEmail);
        sender.setName(brevoSenderName);
        sendSmtpEmail.setSender(sender);


        // 받는 사람 설정
        SendSmtpEmailTo to = new SendSmtpEmailTo();
        to.setEmail(user.getEmail());
        sendSmtpEmail.setTo(Collections.singletonList(to));

        // 제목 및 내용 (HTML 형식)
        sendSmtpEmail.setSubject(subject);
        sendSmtpEmail.setHtmlContent(htmlContent);

        try {
            apiInstance.sendTransacEmail(sendSmtpEmail);
            System.out.println("Email sent successfully ");
        } catch (Exception e) {
            System.err.println("Exception when calling TransactionalEmailsApi: " + e.getMessage());
        }

        /*
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();

        header.setContentType(MediaType.APPLICATION_JSON);
        header.set("api-key", brevoApiKey);

        Map<String, Object> body = new HashMap();
        body.put("sender", brevoSenderEmail);
        body.put("to", user.getEmail());
        body.put("subject", subject);
        body.put("htmlContent", htmlContent);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, header);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.brevo.com/v3/smtp/email",
                    entity,
                    String.class
            );

            logInfo(infoMap.get("type") + " 메일 발송 결과: " + response.getStatusCode());
        } catch (Exception e) {
            logErr(infoMap.get("type") + " 메일 발송 실패: " + e.getMessage());
            throw new RuntimeException(e);
        }
        */

    }
}
