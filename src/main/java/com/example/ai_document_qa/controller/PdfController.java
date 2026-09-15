package com.example.ai_document_qa.controller;

import com.example.ai_document_qa.service.PdfService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/documents")
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
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

        tempFile.delete();

        return text;
    }
}