package com.example.ai_document_qa.controller;

import com.example.ai_document_qa.service.EmbeddingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/embedding")
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    public EmbeddingController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @GetMapping
    public String generateEmbedding(@RequestParam String text) {

        float[] vector = embeddingService.generateEmbedding(text);

        return "Vector dimensions: " + vector.length;
    }
}