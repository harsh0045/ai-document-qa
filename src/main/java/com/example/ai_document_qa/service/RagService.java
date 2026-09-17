package com.example.ai_document_qa.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final SimilaritySearchService similaritySearchService;
    private final ChatClient chatClient;

    public RagService(
            SimilaritySearchService similaritySearchService,
            ChatClient.Builder builder) {

        this.similaritySearchService = similaritySearchService;
        this.chatClient = builder.build();
    }

    public String ask(String question) {

        // 1. Search relevant documents
        List<Document> documents =
                similaritySearchService.search(question);

        // 2. Convert documents into context
        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));

        // 3. Create RAG prompt
        String prompt = """
                Answer the question using only the context provided below.

                If the answer is not present in the context,
                say that you don't know based on the provided context.

                Context:
                %s

                Question:
                %s
                """.formatted(context, question);

        // 4. Send prompt to Gemini
        return chatClient
                .prompt(prompt)
                .call()
                .content();
    }
}