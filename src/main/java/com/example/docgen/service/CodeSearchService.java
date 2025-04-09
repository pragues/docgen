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
    private GithubApiClient gitHubApiClient;

    @Autowired
    private OpenaiClient openaiClient;

    @Autowired
    private LLMService llmService;

    @Autowired
    private RedisTemplate<String, SearchResult> redisTemplate;

    public SearchResult searchAndGenerateDoc(String keyword) {
        // 优先从 Redis 获取
        String cacheKey = "search:" + keyword;
        SearchResult cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) return cached;

        // 调用 GitHub 搜索代码
        SearchResult searchResult = gitHubApiClient.searchCodeSnippet(keyword);

        // 调用 LLM 生成解释
        String explanation = llmService.generateExplanation(searchResult.getCodeSnippet());

        searchResult.setExplanation(explanation);
        redisTemplate.opsForValue().set(cacheKey, searchResult, Duration.ofHours(1));

        return searchResult;
    }
}