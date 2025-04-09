package com.example.docgen.controller;

import com.example.docgen.model.SearchResult;
import com.example.docgen.service.CodeSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
public class CodeSearchController {

    @Autowired
    private CodeSearchService codeSearchService;

    @GetMapping
    public ResponseEntity<SearchResult> searchCode(@RequestParam String keyword) {
        SearchResult result = codeSearchService.searchAndGenerateDoc(keyword);
        return ResponseEntity.ok(result);
    }
}