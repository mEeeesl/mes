package com.min.mes.service.sendSNS.brevo;

import com.min.mes.common.exception.ErrorCode;
import com.min.mes.common.exception.GlobalException;
import com.min.mes.entity.UserEntity;
import com.min.mes.util.RedisUtil;
import com.min.mes.util.StringUtil;
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


    public boolean snedEmail(Map dataMap, UserEntity user) {

        boolean isSuccess = false;

        String type = StringUtil.checkNull(dataMap.get("type"));
        String subject = "";
        String htmlContent = "";

        if("id".equalsIgnoreCase(type)){
            subject = "요청하신 아이디 찾기 결과입니다.";
            htmlContent =
                    "<h3>안녕하세요. mes입니다.</h3>" +
                    "<p>요청하신 아이디 정보를 안내해 드립니다.</p>" +
                    "<p>아이디: <strong>" + user.getUserId() + "</strong></p>";
        } else if("pw".equalsIgnoreCase(type)){
            subject = "요청하신 비밀번호 찾기 결과입니다.";
            htmlContent =
                    "<h3>안녕하세요. mes입니다.</h3>" +
                    "<p>요청하신 비밀번호 정보를 안내해 드립니다.</p>" +
                    "<p>아래의 임시비밀번호로 귀하의 비밀번호를 설정하였으며</p>" +
                    "<p>임시비밀번호로 로그인 후 비밀번호를 변경하여 사용하시기 바랍니다.</p>" +
                    "<p>임시비밀번호: <strong>" + dataMap.get("tmpPw") + "</strong></p>";
        } else if("authChk".equals(type)){
            subject = "[mes] 인증코드 발신";
            htmlContent =
                    "<h3>안녕하세요. mes입니다.</h3>" +
                    "<p>요청하신 아이디 및 비밀번호 정보를 안전하게 안내해드리기위해</p>" +
                    "<p>아래의 인증코드를 당사 사이트에 입력해주세요.</p>" +
                    "<p>해당 인증코드는 1회만 유효합니다.</p>" +
                    "<p>인증코드: <strong>" + dataMap.get("authCode") + "</strong></p>";
        }

        try {

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

            apiInstance.sendTransacEmail(sendSmtpEmail);
            logInfo("Brevo email sent successfully ");

            isSuccess = true;

        } catch (Exception e) {
            logErr(e, e.getMessage());
            new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);

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
        return isSuccess;
    }
}
