package com.example.ai_document_qa.controller;

import com.example.ai_document_qa.service.VectorStoreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vector")
public class VectorController {

    private final VectorStoreService vectorStoreService;

    public VectorController(VectorStoreService vectorStoreService) {
        this.vectorStoreService = vectorStoreService;
    }

    @PostMapping("/save")
    public String save(@RequestBody List<String> chunks) {

        vectorStoreService.saveChunks(chunks);

        return "Chunks saved successfully";
    }
}