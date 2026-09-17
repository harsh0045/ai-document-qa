package com.example.ai_document_qa.controller;

import com.example.ai_document_qa.service.SimilaritySearchService;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SimilaritySearchController {

    private final SimilaritySearchService similaritySearchService;

    public SimilaritySearchController(
            SimilaritySearchService similaritySearchService) {
        this.similaritySearchService = similaritySearchService;
    }

    @GetMapping
    public List<String> search(@RequestParam String query) {

        List<Document> documents =
                similaritySearchService.search(query);

        return documents.stream()
                .map(Document::getText)
                .toList();
    }
}