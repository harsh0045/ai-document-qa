# 🤖 AI Document Q&A — RAG with Spring AI

A beginner-friendly **Retrieval-Augmented Generation (RAG)** project built using **Java, Spring Boot, Spring AI, PostgreSQL, PGVector, Ollama, and Google Gemini**.

The goal of this project is to understand how modern AI document-question-answering systems work by building the complete RAG pipeline step by step.

---

## 🚀 Project Goal

Build an application where users can upload documents and ask questions about their content.

The application follows this architecture:

```text
PDF
 ↓
Text Extraction
 ↓
Text Chunking
 ↓
Embeddings
 ↓
Vector Database
 ↓
Similarity Search
 ↓
Relevant Chunks
 ↓
Context + Question
 ↓
Gemini LLM
 ↓
Final Answer
```

---

# 🛠️ Tech Stack

* Java 21
* Spring Boot 3.4.1
* Spring AI 1.1.2
* Google Gemini
* Ollama
* embeddinggemma
* PostgreSQL
* PGVector
* Apache PDFBox
* Maven
* Docker
* Postman

---

# 📚 Learning Progress

| Lesson   | Topic                       | Status      |
| -------- | --------------------------- | ----------- |
| Lesson 1 | Spring Boot Setup           | ✅ Completed |
| Lesson 2 | LLM Integration with Gemini | ✅ Completed |
| Lesson 3 | PDF Text Extraction         | ✅ Completed |
| Lesson 4 | Text Chunking               | ✅ Completed |
| Lesson 5 | Embeddings with Ollama      | ✅ Completed |
| Lesson 6 | PostgreSQL + PGVector       | ✅ Completed |
| Lesson 7 | Similarity Search           | ✅ Completed |
| Lesson 8 | Complete RAG Pipeline       | ✅ Completed |

---

# 📖 Lesson 1 — Spring Boot Setup

Created a basic Spring Boot application and learned:

* Spring Boot project structure
* REST Controllers
* GET endpoints
* Maven
* Running a Spring Boot application

Example:

```java
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello AI";
    }
}
```

Test:

```text
GET http://localhost:8080/hello
```

---

# 🧠 Lesson 2 — LLM Integration with Gemini

Integrated **Google Gemini** using Spring AI.

The application can send a question to Gemini and receive an AI-generated response.

### Configuration

```properties
spring.ai.google.genai.api-key=${GEMINI_API_KEY}
spring.ai.google.genai.chat.options.model=gemini-3.6-flash
spring.ai.model.chat=google-genai
```

API:

```text
GET http://localhost:8080/ai?question=What is Kafka?
```

### Basic Flow

```text
User Question
      ↓
Spring Boot
      ↓
Spring AI
      ↓
Gemini
      ↓
AI Response
```

---

# 📄 Lesson 3 — PDF Text Extraction

Added Apache PDFBox to extract text from uploaded PDF documents.

### Dependency

```xml
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>3.0.6</version>
</dependency>
```

Created:

```text
PdfService
PdfController
```

The service loads a PDF and extracts its text.

### API

```text
POST http://localhost:8080/documents/upload
```

In Postman:

```text
Body → form-data

key: file
type: File
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

---

# ✂️ Lesson 4 — Text Chunking

Large documents cannot always be sent directly to an LLM.

Therefore, the extracted document text is divided into smaller pieces called **chunks**.

Created:

```text
TextChunker
```

Current implementation uses a simple character-based chunking approach.

```java
public List<String> splitText(String text, int chunkSize) {

    List<String> chunks = new ArrayList<>();

    for (int start = 0; start < text.length(); start += chunkSize) {

        int end = Math.min(start + chunkSize, text.length());

        chunks.add(text.substring(start, end));
    }

    return chunks;
}
```

Current chunk size:

```text
1000 characters
```

### Example

```text
Large Document
      ↓
Chunk 1
Chunk 2
Chunk 3
Chunk 4
...
```

### Important

This is a simple implementation for learning.

Production RAG systems generally use smarter chunking strategies that consider:

* sentences
* paragraphs
* sections
* chunk overlap
* document structure

---

# 🔢 Lesson 5 — Embeddings

An embedding converts text into a numerical vector.

For example:

```text
"Java is a programming language."
             ↓
       Embedding Model
             ↓
      [0.12, -0.43, 0.87, ...]
```

These vectors allow the application to compare the **semantic meaning** of text.

For this project:

```text
Embedding Provider → Ollama
Embedding Model    → embeddinggemma
```

### Configuration

```properties
spring.ai.ollama.base-url=http://localhost:11434

spring.ai.model.embedding=ollama
spring.ai.ollama.embedding.options.model=embeddinggemma
```

### Embedding Service

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

### Test

```text
GET http://localhost:8080/embedding?text=Java is a programming language
```

---

# 🗄️ Lesson 6 — PostgreSQL + PGVector

Added PostgreSQL with the **PGVector** extension.

PGVector allows PostgreSQL to store and search vector embeddings.

### Docker Container

```text
Container Name: rag-postgres
Database: ragdb
Username: postgres
Password: postgres
```

Docker port mapping:

```text
Windows: 5434
Docker: 5432
```

Run:

```powershell
docker run --name rag-postgres `
  -e POSTGRES_USER=postgres `
  -e POSTGRES_PASSWORD=postgres `
  -e POSTGRES_DB=ragdb `
  -p 5434:5432 `
  -d pgvector/pgvector:pg17
```

Enable the vector extension:

```powershell
docker exec -it rag-postgres psql -U postgres -d ragdb
```

Then:

```sql
CREATE EXTENSION IF NOT EXISTS vector;
```

### Spring Configuration

```properties
spring.datasource.url=jdbc:postgresql://127.0.0.1:5434/ragdb
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.ai.vectorstore.pgvector.initialize-schema=true
```

---

# 📦 Storing Documents in PGVector

Created:

```text
VectorStoreService
VectorController
```

Documents are converted into Spring AI `Document` objects:

```java
List<Document> documents = chunks.stream()
        .map(Document::new)
        .toList();

vectorStore.add(documents);
```

API:

```text
POST http://localhost:8080/vector/save
```

Example:

```json
[
  "Java is a programming language.",
  "Spring Boot is used to build Java backend applications.",
  "Kafka is a distributed event streaming platform."
]
```

---

# 🔍 Lesson 7 — Similarity Search

Now we can search the vector database for documents that are semantically similar to a user's question.

Created:

```text
SimilaritySearchService
SimilaritySearchController
```

### Search Request

```java
SearchRequest searchRequest = SearchRequest.builder()
        .query(query)
        .topK(1)
        .build();

return vectorStore.similaritySearch(searchRequest);
```

### What is Top-K?

`topK` specifies how many relevant documents should be returned.

For example:

```text
topK(1)
```

means:

```text
Return the 1 most relevant document.
```

With:

```text
topK(3)
```

the system can return the 3 most relevant documents.

### API

```text
GET http://localhost:8080/search?query=What is Kafka?
```

Example result:

```json
[
    "Kafka is a distributed event streaming platform."
]
```

### Retrieval Flow

```text
User Query
    ↓
Query Embedding
    ↓
PGVector
    ↓
Similarity Comparison
    ↓
Top-K Relevant Documents
```

---

# 🤖 Lesson 8 — Complete RAG Pipeline

This lesson connects everything together.

The application now performs the complete **Retrieval-Augmented Generation** process.

## Architecture

```text
                 User Question
                       ↓
                Similarity Search
                       ↓
                Relevant Chunks
                       ↓
                 Build Context
                       ↓
              Context + Question
                       ↓
                    Gemini
                       ↓
                 Final Answer
```

---

## 1. Retrieval

The question is first sent to the similarity search service.

```java
List<Document> documents =
        similaritySearchService.search(question);
```

This retrieves the most relevant document chunks from PGVector.

---

## 2. Build Context

The retrieved documents are converted into a single context string.

```java
String context = documents.stream()
        .map(Document::getText)
        .collect(Collectors.joining("\n\n"));
```

For example:

```text
Context:

Kafka is a distributed event streaming platform.
```

---

## 3. Create the RAG Prompt

The context and user's question are combined:

```java
String prompt = """
        Answer the question using only the context provided below.

        If the answer is not present in the context,
        say that you don't know based on the provided context.

        Context:
        %s

        Question:
        %s
        """.formatted(context, question);
```

This is the **Augmentation** part of RAG.

---

## 4. Send Context to Gemini

The final prompt is sent to Gemini:

```java
return chatClient
        .prompt(prompt)
        .call()
        .content();
```

Gemini generates the final answer using the retrieved context.

---

# 🔄 Complete RAG Flow

Suppose the user asks:

```text
What is Kafka?
```

The application performs:

```text
"What is Kafka?"
        ↓
Generate query embedding
        ↓
Search PGVector
        ↓
Find relevant chunk
        ↓
"Kafka is a distributed event streaming platform."
        ↓
Build prompt
        ↓
Send context + question to Gemini
        ↓
Generate answer
```

Example response:

```text
Kafka is a distributed event streaming platform.
```

---

# 🧪 Testing the RAG API

Start the application:

```powershell
.\mvnw clean
.\mvnw spring-boot:run
```

Then:

```text
GET http://localhost:8080/rag?question=What is Kafka?
```

You can also test a question that is not contained in the stored documents:

```text
GET http://localhost:8080/rag?question=What is Python?
```

The prompt instructs Gemini to say that it does not know based on the provided context when the answer is not present.

---

# 🧩 Final Project Architecture

```text
                    ┌─────────────────┐
                    │      User       │
                    └────────┬────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │ Spring Boot API │
                    └────────┬────────┘
                             │
                ┌────────────┴────────────┐
                │                         │
                ▼                         ▼
        Document Upload             User Question
                │                         │
                ▼                         ▼
           PDFBox                    Embedding
                │                         │
                ▼                         ▼
          Text Chunking              PGVector
                │                         │
                ▼                         ▼
            Embedding              Similarity Search
                │                         │
                ▼                         ▼
             PGVector              Relevant Chunks
                                          │
                                          ▼
                                     Build Context
                                          │
                                          ▼
                                   Gemini 3.6 Flash
                                          │
                                          ▼
                                     Final Answer
```

---

# 📁 Important Project Structure

```text
src/
└── main/
    └── java/
        └── com/example/ai_document_qa/
            │
            ├── controller/
            │   ├── AiController.java
            │   ├── PdfController.java
            │   ├── EmbeddingController.java
            │   ├── VectorController.java
            │   ├── SimilaritySearchController.java
            │   └── RagController.java
            │
            └── service/
                ├── PdfService.java
                ├── TextChunker.java
                ├── EmbeddingService.java
                ├── VectorStoreService.java
                ├── SimilaritySearchService.java
                └── RagService.java
```

---

# ⚙️ Application Configuration

Current important configuration:

```properties
spring.application.name=ai-document-qa

# PostgreSQL
spring.datasource.url=jdbc:postgresql://127.0.0.1:5434/ragdb
spring.datasource.username=postgres
spring.datasource.password=postgres

# PGVector
spring.ai.vectorstore.pgvector.initialize-schema=true

# Gemini Chat
spring.ai.google.genai.api-key=${GEMINI_API_KEY}
spring.ai.google.genai.chat.options.model=gemini-3.6-flash
spring.ai.model.chat=google-genai

# Ollama Embeddings
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.model.embedding=ollama
spring.ai.ollama.embedding.options.model=embeddinggemma
```

---

# 🔐 Environment Variables

Create a `.env` file:

```env
GEMINI_API_KEY=your_api_key
```

Do **not** commit your API key to GitHub.

`.gitignore`:

```gitignore
.env
target/
.idea/
*.iml
.vscode/
```

---

# 🧠 Key Concepts Learned

Through Lessons 1–8, this project covers:

* Spring Boot REST APIs
* LLMs
* Google Gemini
* Spring AI
* PDF processing
* Text chunking
* Embeddings
* Vector databases
* PostgreSQL
* PGVector
* Ollama
* Semantic similarity search
* Top-K retrieval
* Context injection
* Prompt construction
* Retrieval-Augmented Generation

---

# 🎯 Current Status

The project now has a working basic RAG pipeline:

```text
PDF
 ↓
Extract Text
 ↓
Chunk Text
 ↓
Generate Embeddings
 ↓
Store in PGVector
 ↓
Similarity Search
 ↓
Retrieve Relevant Chunks
 ↓
Build Context
 ↓
Send to Gemini
 ↓
Generate Answer
```

## ✅ Lessons 1–8 Completed

The fundamental RAG workflow is now implemented manually so that each individual step can be understood.

---

# 🚀 Future Improvements

Possible next lessons:

* Improve chunking with overlap
* Better semantic chunking
* Automatically process uploaded PDFs
* Automatically generate embeddings
* Automatically save documents to PGVector
* Metadata and source tracking
* Similarity thresholds
* Retrieve multiple chunks with Top-K
* Improve RAG prompts
* Spring AI RAG Advisors
* `QuestionAnswerAdvisor`
* Source citations
* Conversation memory
* Multiple document support
* RAG evaluation
* Production-ready architecture
* Dockerize the complete application
* Build a frontend for document upload and Q&A

---

# 📌 What I Learned

The most important concept from this project is:

```text
LLM alone
    ↓
Generates answers from its existing knowledge

RAG
    ↓
Retrieve relevant information
    ↓
Give that information to the LLM
    ↓
Generate a grounded answer
```

In simple terms:

> **RAG = Search relevant information + Give it to the LLM + Generate an answer**

This project is built step by step to understand what happens inside a RAG application instead of directly using a high-level abstraction.

