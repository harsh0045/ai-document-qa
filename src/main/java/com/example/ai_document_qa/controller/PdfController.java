package com.example.ai_document_qa.controller;

import com.example.ai_document_qa.service.PdfService;
import com.example.ai_document_qa.service.TextChunker;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/documents")
public class PdfController {

    private final PdfService pdfService;
    private final TextChunker textChunker;

    public PdfController(
            PdfService pdfService,
            TextChunker textChunker) {

        this.pdfService = pdfService;
        this.textChunker = textChunker;
    }

    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public String upload(@RequestParam("file") MultipartFile file)
            throws IOException {

        File tempFile = File.createTempFile("upload-", ".pdf");

        file.transferTo(tempFile);

        String text = pdfService.extractText(tempFile);

        List<String> chunks = textChunker.splitText(text, 1000);

        tempFile.delete();

        return chunks.toString();
    }
}