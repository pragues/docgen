package com.example.docgen.util;

import com.example.docgen.model.SearchResult;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class GithubApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String TOKEN = "YOUR_GITHUB_TOKEN";
    private static final String BASE_URL = "https://api.github.com/search/code";

    public SearchResult searchCodeSnippet(String keyword) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(TOKEN);
        headers.set("Accept", "application/vnd.github.v3+json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String query = String.format("%s+in:file+language:java", URLEncoder.encode(keyword, StandardCharsets.UTF_8));
        String url = "https://api.github.com/search/code?q=" + query + "&per_page=10";

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        List<Map> items = (List<Map>) response.getBody().get("items");

        if (items != null && !items.isEmpty()) {
            Map firstItem = items.get(0);
            String htmlUrl = (String) firstItem.get("html_url");
            Map repository = (Map) firstItem.get("repository");
            String owner = ((Map) repository.get("owner")).get("login").toString();
            String repo = repository.get("name").toString();
            String path = firstItem.get("path").toString();

            String rawContent = getFileContent(owner, repo, path);
            SearchResult result = new SearchResult(keyword, htmlUrl, rawContent, url);

            // 提取前10个搜索结果的简略信息
            List<Map<String, String>> summaries = new ArrayList<>();
            for (Map item : items) {
                Map repoInfo = (Map) item.get("repository");
                Map<String, String> summary = new HashMap<>();
                summary.put("name", item.get("name").toString());
                summary.put("path", item.get("path").toString());
                summary.put("repository", repoInfo.get("full_name").toString());
                summary.put("html_url", item.get("html_url").toString());
                summaries.add(summary);
            }

            result.setTopResults(summaries);
            return result;
        }

        return new SearchResult(keyword, "未找到匹配的代码。", "", url);
    }

    private String buildQuery(String keyword, String language, String repo) {
        StringBuilder query = new StringBuilder();

        if (keyword != null && !keyword.isEmpty()) {
            query.append(keyword).append("+in:file");
        }

        if (language != null && !language.isEmpty()) {
            query.append("+language:").append(language);
        }

        if (repo != null && !repo.isEmpty()) {
            query.append("+repo:").append(repo);
        }

        return query.toString();
    }

    private String getFileContent(String owner, String repo, String path) {
        String url = String.format("https://api.github.com/repos/%s/%s/contents/%s", owner, repo, path);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(TOKEN);
        headers.set("Accept", "application/vnd.github.v3+json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            String encodedContent = (String) response.getBody().get("content");
            return new String(Base64.getDecoder().decode(encodedContent));
        }

        return "无法获取文件内容。";
    }
}