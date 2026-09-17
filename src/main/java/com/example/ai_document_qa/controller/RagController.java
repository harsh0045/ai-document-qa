package com.example.ai_document_qa.controller;

import com.example.ai_document_qa.service.RagService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @GetMapping
    public String ask(@RequestParam String question) {
        return ragService.ask(question);
    }
}