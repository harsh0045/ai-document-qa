package com.example.ai_document_qa.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextChunker {

    public List<String> splitText(String text, int chunkSize) {

        List<String> chunks = new ArrayList<>();

        for (int start = 0; start < text.length(); start += chunkSize) {

            int end = Math.min(start + chunkSize, text.length());

            chunks.add(text.substring(start, end));
        }

        return chunks;
    }
}