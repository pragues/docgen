package com.example.docgen.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class OpenaiClient {

    @Value("${qwen.key}")
    private String dashscopeKey;

    private static final String OPENAI_API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateExplanation(String codeSnippet) {
        // 构建请求体，格式与 OpenAI 一致
        Map<String, Object> request = Map.of(
                "model", "qwen-plus",  // 阿里千问模型
                "messages", List.of(
                        Map.of("role", "system", "content", "你是一个经验丰富的软件工程师，请用简洁自然的语言解释以下代码："),
                        Map.of("role", "user", "content", codeSnippet)
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(dashscopeKey); // 这里传的是阿里的 key

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_API_URL, entity, Map.class);

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return message.get("content").toString().trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "调用 Qwen 接口失败：" + e.getMessage();
        }

        System.out.println(">>> 请求体: " + entity);
        System.out.println(">>> 请求 URL: " + OPENAI_API_URL);
        System.out.println(">>> 请求 headers: " + headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_API_URL, entity, Map.class);
        System.out.println(">>> 响应状态: " + response.getStatusCode());
        System.out.println(">>> 响应体: " + response.getBody());

        return "Qwen 无响应。";
    }
}