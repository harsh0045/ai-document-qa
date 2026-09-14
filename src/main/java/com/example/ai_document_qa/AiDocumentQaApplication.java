package com.example.ai_document_qa;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AiDocumentQaApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();

		String geminiApiKey = dotenv.get("GEMINI_API_KEY");

		if (geminiApiKey != null) {
			System.setProperty("GEMINI_API_KEY", geminiApiKey);
		}

		SpringApplication.run(AiDocumentQaApplication.class, args);
	}
}