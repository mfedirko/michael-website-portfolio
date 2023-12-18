package io.mfedirko.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class RecaptchaClient {
    private final String secretKey;
    private final RestTemplate restTemplate;

    public RecaptchaClient(@Value("${recaptcha.secret-key}") String secretKey,
                           RestTemplateBuilder restTemplate) {
        this.secretKey = secretKey;
        this.restTemplate = restTemplate.build();
    }

    public boolean isValidCaptcha(String captcha) {
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    UriComponentsBuilder.fromHttpUrl("https://www.google.com/recaptcha/api/siteverify")
                    .queryParam("secret", secretKey)
                    .queryParam("response", captcha)
                    .build().toUri(), null, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception ex) {
            return false;
        }
    }
}
