package com.example.ai_document_qa.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimilaritySearchService {

    private final VectorStore vectorStore;

    public SimilaritySearchService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<Document> search(String query) {

        SearchRequest searchRequest = SearchRequest.builder()
                .query(query)
                .topK(1)
                .build();

        return vectorStore.similaritySearch(searchRequest);
    }
}