package com.example.docgen.model;


import java.util.Map;
import java.util.List;


public class SearchResult {
    private String keyword;
    private String codeSnippet;
    private String explanation;

    private String url;
    private List<Map<String, String>> topResults;


    public SearchResult() {}

    public SearchResult(String keyword, String codeSnippet, String expla, String url) {
        this.keyword = keyword;
        this.explanation = expla;
        this.codeSnippet=codeSnippet;
        this.url = url;
    }


    public String getCodeSnippet() {
        return codeSnippet;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getKeyword() {
        return keyword;
    }

    public List<Map<String, String>> getTopResults() {
        return topResults;
    }

    public String getUrl() {
        return url;
    }

    public void setCodeSnippet(String codeSnippet) {
        this.codeSnippet = codeSnippet;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setTopResults(List<Map<String, String>> topResults) {
        this.topResults = topResults;
    }
}