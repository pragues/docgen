package com.example.docgen.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class LLMService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${qwen.key}")
    private String openaiApiKey;

    private static final String OPENAI_API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";

    public String generateExplanation(String codeSnippet) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey); // 使用注入的值

        Map<String, Object> request = Map.of(
                "model", "qwen-plus", // 阿里千问的模型名
                "messages", List.of(
                        Map.of("role", "system", "content", "你是一个资深程序员，请解释下面的相关的搜索结果"),
                        Map.of("role", "user", "content", codeSnippet)
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_API_URL, entity, Map.class);
            List<Map> choices = (List<Map>) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map message = (Map) choices.get(0).get("message");
                return message.get("content").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "调用千问 API 失败：" + e.getMessage();
        }

        return "Qwen 无响应。";
    }
}