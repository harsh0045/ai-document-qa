# AI Document Q&A — RAG with Spring AI

A beginner-friendly **Retrieval-Augmented Generation (RAG)** application built using **Spring Boot, Spring AI, Gemini, Ollama, and a Vector Database**.

The goal of this project is to understand how modern AI/RAG applications work by building the system step by step.

---

## 🚀 Tech Stack

* Java 21
* Spring Boot 3.4.1
* Spring AI 1.1.2
* Spring Web
* Google Gemini
* Ollama
* Apache PDFBox
* Vector Database *(coming soon)*
* Maven

---

# 🧠 What is RAG?

RAG stands for **Retrieval-Augmented Generation**.

Instead of asking an LLM to answer only from its existing knowledge, we first retrieve relevant information from our own documents and provide that information to the LLM.

### RAG Flow

```text
PDF
 ↓
Extract Text
 ↓
Split into Chunks
 ↓
Generate Embeddings
 ↓
Store Vectors
 ↓
User Question
 ↓
Similarity Search
 ↓
Retrieve Relevant Chunks
 ↓
LLM
 ↓
Final Answer
```

---

# 📚 Lessons

## Lesson 1 — Spring Boot Project Setup

### Goal

Create the basic Spring Boot application and verify that the application is working.

### What We Learned

* Create a Spring Boot project
* Configure Maven
* Run a Spring Boot application
* Create REST controllers
* Create a basic GET endpoint

### Example

```java
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello AI!";
    }
}
```

### Test

```text
GET http://localhost:8080/hello
```

---

# Lesson 2 — LLM Integration with Spring AI + Gemini

### Goal

Connect our Spring Boot application with an LLM and generate AI responses.

For this project we use:

```text
Spring AI
    ↓
Google Gemini
    ↓
gemini-3.6-flash
```

### Configuration

```properties
spring.ai.google.genai.api-key=${GEMINI_API_KEY}
spring.ai.google.genai.chat.options.model=gemini-3.6-flash
```

The API key is stored in `.env`:

```env
GEMINI_API_KEY=your_api_key
```

### Chat Flow

```text
User Question
      ↓
Spring AI ChatClient
      ↓
Gemini
      ↓
AI Response
```

### Example Endpoint

```text
GET http://localhost:8080/ai?question=What is Kafka?
```

### Important Concept

An **LLM (Large Language Model)** generates text based on the input prompt.

In our application:

```text
Question → Gemini → Answer
```

---

# Lesson 3 — PDF Upload and Text Extraction

### Goal

Allow the application to receive a PDF and extract its text.

We use **Apache PDFBox** for PDF text extraction.

### Dependency

```xml
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>3.0.6</version>
</dependency>
```

### Flow

```text
PDF
 ↓
MultipartFile
 ↓
Temporary File
 ↓
PDFBox
 ↓
Extracted Text
```

### Endpoint

```text
POST http://localhost:8080/documents/upload
```

### Postman

```text
Body
 → form-data
 → key: file
 → type: File
 → select PDF
```

### Important Concept

Before we can build RAG, we need to convert the document into plain text.

So:

```text
PDF → Text
```

---

# Lesson 4 — Text Chunking

### Goal

Split the extracted PDF text into smaller pieces called **chunks**.

Instead of sending an entire document to an LLM or embedding model, we divide it into manageable pieces.

### Why Chunking?

For example:

```text
Large PDF
   ↓
1000 characters
   ↓
Chunk 1

1000 characters
   ↓
Chunk 2

1000 characters
   ↓
Chunk 3
```

This makes it possible to later search for only the relevant parts of a document.

### Simple Chunker

```java
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
```

### Current Configuration

```text
Chunk Size = 1000 characters
```

### Flow

```text
PDF
 ↓
Extract Text
 ↓
Text Chunker
 ↓
Chunk 1
Chunk 2
Chunk 3
...
```

### Important Note

The current implementation is a **simple character-based chunker**.

It can split a sentence in the middle.

Later, this can be improved using:

* Chunk overlap
* Sentence-based splitting
* Paragraph-based splitting
* Token-aware splitting

For learning purposes, the simple chunker is sufficient at this stage.

---

# Lesson 5 — Embeddings with Ollama

## 🎯 Goal

Convert text chunks into **numerical vectors** called embeddings.

These vectors will later be stored in a vector database and used for semantic similarity search.

---

## What is an Embedding?

An embedding is a numerical representation of text that captures its meaning.

```text
Text
 ↓
Embedding Model
 ↓
Vector
```

For example:

```text
"Java is a programming language"
```

might become something conceptually like:

```text
[0.12, -0.45, 0.78, 0.21, ...]
```

The actual vector contains many numbers.

---

## Why Do We Need Embeddings?

RAG needs to find documents that are **semantically similar** to a user's question.

For example:

```text
Document:
"Kafka is used for event streaming."

Question:
"What is Kafka used for?"
```

Even though the exact words may differ, their meanings are related.

Embeddings allow us to compare this meaning mathematically.

---

# Chat Model vs Embedding Model

This project uses **two different AI models for two different jobs**.

### Gemini — Chat

Gemini is responsible for generating the final answer.

```text
Question + Retrieved Context
          ↓
       Gemini
          ↓
       Answer
```

### Ollama — Embeddings

Ollama is responsible for converting text into vectors.

```text
Text Chunk
    ↓
Ollama
    ↓
embeddinggemma
    ↓
Vector
```

Therefore:

```text
Gemini → Generate answers

Ollama → Generate embeddings
```

---

# Why Ollama for Embeddings?

Google Gemini embeddings required additional Google Cloud configuration for this setup.

For learning purposes, Ollama allows us to run the embedding model locally.

This gives us:

* Local embedding generation
* No separate cloud embedding service
* Easy experimentation
* Same Spring AI application

---

# Step 1 — Install Ollama

Check whether Ollama is installed:

```bash
ollama --version
```

---

# Step 2 — Download Embedding Model

Pull the embedding model:

```bash
ollama pull embeddinggemma
```

Check installed models:

```bash
ollama list
```

You should see:

```text
embeddinggemma
```

---

# Step 3 — Add Ollama Dependency

Add the Spring AI Ollama starter to `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-ollama</artifactId>
</dependency>
```

We keep Gemini because Gemini is still being used for chat:

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-google-genai</artifactId>
</dependency>
```

### Important

Do **not** keep the Google GenAI embedding starter because embeddings are now handled by Ollama.

---

# Step 4 — Configure Gemini + Ollama

`application.properties`:

```properties
spring.application.name=ai-document-qa

# =========================
# Gemini - Chat Model
# =========================
spring.ai.google.genai.api-key=${GEMINI_API_KEY}
spring.ai.google.genai.chat.options.model=gemini-3.6-flash

# =========================
# Ollama - Embedding Model
# =========================
spring.ai.ollama.base-url=http://localhost:11434

spring.ai.model.embedding=ollama
spring.ai.ollama.embedding.options.model=embeddinggemma

# Use Gemini for Chat
spring.ai.model.chat=google-genai
```

### Important

We explicitly tell Spring AI:

```properties
spring.ai.model.chat=google-genai
```

and:

```properties
spring.ai.model.embedding=ollama
```

This is important because both Gemini and Ollama can provide chat models.

Without selecting the chat model, Spring may find:

```text
googleGenAiChatModel
ollamaChatModel
```

and fail because it does not know which one to use.

---

# Step 5 — Create Embedding Service

```java
@Service
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public float[] generateEmbedding(String text) {
        return embeddingModel.embed(text);
    }
}
```

### What happens here?

```text
Text
 ↓
EmbeddingModel
 ↓
Ollama
 ↓
embeddinggemma
 ↓
float[]
```

---

# Step 6 — Create Embedding Controller

```java
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
```

---

# Step 7 — Run the Application

Clean the project:

```bash
./mvnw clean
```

Start Spring Boot:

```bash
./mvnw spring-boot:run
```

---

# Step 8 — Test Embeddings

Use:

```text
GET http://localhost:8080/embedding?text=Java is a programming language
```

Expected response:

```text
Vector dimensions: ...
```

The exact number of dimensions depends on the embedding model.

---

# Complete Lesson 5 Flow

```text
Text Chunk
    ↓
EmbeddingService
    ↓
Spring AI EmbeddingModel
    ↓
Ollama
    ↓
embeddinggemma
    ↓
Vector
```

---

# Current RAG Architecture

After completing Lesson 5, our application looks like:

```text
                    ┌─────────────────────┐
                    │   Gemini 3.6 Flash  │
                    │      Chat Model     │
                    └──────────▲──────────┘
                               │
                               │
PDF → PDFBox → Text → Chunks ──┼──→ RAG
                               │
                               ▼
                    ┌─────────────────────┐
                    │      Ollama         │
                    │   embeddinggemma   │
                    │  Embedding Model    │
                    └─────────────────────┘
                               │
                               ▼
                            Vectors
```

---

# 🗺️ RAG Learning Progress

```text
Lesson 1
Spring Boot
    ↓
Lesson 2
Spring AI + Gemini
    ↓
Lesson 3
PDF → Text
    ↓
Lesson 4
Text → Chunks
    ↓
Lesson 5
Chunks → Ollama Embeddings
    ↓
Lesson 6
Vector Database
    ↓
Lesson 7
Similarity Search
    ↓
Lesson 8
Complete RAG
```

---

# 🎯 What We Have Learned So Far

By the end of Lesson 5, we understand:

* Spring Boot REST APIs
* Spring AI basics
* LLM integration
* Gemini chat model
* PDF text extraction
* Text chunking
* Embeddings
* Embedding models
* Ollama
* `embeddinggemma`
* Difference between Chat Models and Embedding Models
* Converting text into vectors

---

# 🔜 Next Lesson

## Lesson 6 — Vector Database

In the next lesson we will learn:

```text
Chunks
   ↓
Embeddings
   ↓
Vector Database
```

We will store our document embeddings so that we can later search them using a user's question.

The complete RAG pipeline will eventually become:

```text
PDF
 ↓
Text Extraction
 ↓
Chunking
 ↓
Embeddings
 ↓
Vector Database
 ↓
Similarity Search
 ↓
Relevant Context
 ↓
Gemini
 ↓
Final Answer
```

