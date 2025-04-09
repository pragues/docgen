
package com.example.docgen.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class LLMService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String OPENAI_API_KEY = "YOUR_OPENAI_KEY";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public String generateExplanation(String codeSnippet) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(OPENAI_API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> request = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "system", "content", "你是一个资深程序员，请解释下面的代码："),
                        Map.of("role", "user", "content", codeSnippet)
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_API_URL, entity, Map.class);
        List<Map> choices = (List<Map>) response.getBody().get("choices");
        if (choices != null && !choices.isEmpty()) {
            Map choice = choices.get(0);
            Map message = (Map) choice.get("message");
            return message.get("content").toString();
        }

        return "无法生成解释。";
    }
}