package com.example.docgen.service;


import com.example.docgen.util.OpenaiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.docgen.model.SearchResult;
import com.example.docgen.util.GithubApiClient;

import java.time.Duration;


@Service
public class CodeSearchService {

    @Autowired
    private GithubApiClient githubApiClient;

    @Autowired
    private OpenaiClient openaiClient;

    @Autowired
    private LLMService llmService;

    @Autowired
    private RedisTemplate<String, SearchResult> redisTemplate;

    public SearchResult searchAndGenerateDoc(String keyword) {
        // 先从 Redis 获取缓存，如果没有则从 GitHub 和 OpenAI 获取
        String cacheKey = "search:" + keyword;
        SearchResult cached = null;
        try {
            cached = redisTemplate.opsForValue().get(cacheKey);
        } catch (Exception e) {
            System.out.println("Redis 连接失败，使用默认逻辑：" + e.getMessage());
        }

        if (cached != null) {
            return cached;
        }

        // 调用 GitHub 搜索代码
        SearchResult result = githubApiClient.searchCodeSnippet(keyword);

        // 调用 OpenAI 生成解释
        String doc = "【Summary not implemented yet】";
        doc=llmService.generateExplanation(result.getCodeSnippet());

        result.setExplanation(doc);

        // 尝试将结果缓存到 Redis
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(cacheKey, result, Duration.ofHours(1));
            }
        } catch (Exception e) {
            System.out.println("Redis 缓存失败：" + e.getMessage());
        }

        return result;
    }
}