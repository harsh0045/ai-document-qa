# 🤖 AI Document Q&A — Spring AI + RAG

A step-by-step learning project to understand how **LLMs, embeddings, vector databases, and RAG (Retrieval-Augmented Generation)** work together using Java and Spring Boot.

---

## 🚀 Project Goal

Build a simple application where a user can upload a document and ask questions about its content.

### Final RAG Architecture

```text
                    PDF Document
                         │
                         ▼
                  Text Extraction
                         │
                         ▼
                     Chunking
                         │
                         ▼
                    Embeddings
                         │
                         ▼
                  PostgreSQL
                    + pgvector
                         │
                         │
User Question ───────────┘
       │
       ▼
 Question Embedding
       │
       ▼
 Similarity Search
       │
       ▼
 Relevant Chunks
       │
       ▼
      LLM
   Gemini 3.6 Flash
       │
       ▼
     Answer
```

---

# 🛠️ Tech Stack

* Java 21
* Spring Boot 3.4.1
* Spring AI 1.1.2
* Google Gemini — Chat Model
* Ollama — Embedding Model
* `embeddinggemma` — Embedding Model
* PostgreSQL
* pgvector
* Apache PDFBox
* Maven
* Docker
* IntelliJ IDEA
* Postman

---

# 📚 Learning Progress

* [x] Lesson 1 — Spring Boot Setup
* [x] Lesson 2 — LLM Integration with Gemini
* [x] Lesson 3 — PDF Text Extraction
* [x] Lesson 4 — Text Chunking
* [x] Lesson 5 — Embeddings
* [x] Lesson 6 — PostgreSQL + pgvector
* [ ] Lesson 7 — Similarity Search
* [ ] Lesson 8 — Complete RAG Pipeline

---

# 📖 Lesson 1 — Spring Boot Setup

The first step was creating a basic Spring Boot application and understanding how a REST API works.

### Basic Endpoint

```java
@GetMapping("/hello")
public String hello() {
    return "Hello AI";
}
```

### Run the application

```powershell
.\mvnw spring-boot:run
```

Test:

```text
GET http://localhost:8080/hello
```

---

# 🧠 Lesson 2 — LLM Integration

In this lesson, Spring AI was connected to **Google Gemini**.

The `ChatClient` provides a simple interface for sending prompts to an LLM.

### Controller

```java
@RestController
public class AiController {

    private final ChatClient chatClient;

    public AiController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/ai")
    public String askAI(@RequestParam String question) {
        return chatClient
                .prompt(question)
                .call()
                .content();
    }
}
```

### Configuration

```properties
spring.ai.google.genai.api-key=${GEMINI_API_KEY}
spring.ai.google.genai.chat.options.model=gemini-3.6-flash
spring.ai.model.chat=google-genai
```

### Environment Variable

`.env`

```env
GEMINI_API_KEY=your_api_key
```

The `.env` file should never be committed to GitHub.

### Test

```text
GET http://localhost:8080/ai?question=What is Kafka?
```

### Key Concept

```text
User Question
      ↓
Spring AI ChatClient
      ↓
Gemini
      ↓
AI Response
```

---

# 📄 Lesson 3 — PDF Text Extraction

The next step was extracting text from uploaded PDF documents.

Apache PDFBox was used for PDF processing.

### Dependency

```xml
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>3.0.6</version>
</dependency>
```

### PDF Service

```java
@Service
public class PdfService {

    public String extractText(File file) throws IOException {

        try (PDDocument document = Loader.loadPDF(file)) {

            PDFTextStripper stripper = new PDFTextStripper();

            return stripper.getText(document);
        }
    }
}
```

### Flow

```text
PDF
 ↓
PDFBox
 ↓
Extracted Text
```

### Endpoint

```text
POST http://localhost:8080/documents/upload
```

In Postman:

```text
Body
 → form-data
 → key: file
 → type: File
 → select PDF
```

---

# ✂️ Lesson 4 — Text Chunking

Large documents cannot simply be sent directly to an LLM.

Therefore, the extracted text is divided into smaller pieces called **chunks**.

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

Currently, the project uses:

```text
Chunk size = 1000 characters
```

### Example

```text
Large Document
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

### Important

This is a simple character-based chunking approach for learning.

In a production RAG system, chunking can be improved using:

* Sentence boundaries
* Paragraph boundaries
* Chunk overlap
* Token-based chunking
* Semantic chunking

---

# 🔢 Lesson 5 — Embeddings

An embedding converts text into a numerical vector.

For example:

```text
"Java is a programming language"
              ↓
         Embedding Model
              ↓
[0.12, -0.45, 0.78, ...]
```

The important idea is that **similar meanings produce similar vectors**.

### Embedding Model

Google Gemini was used for chat, but Ollama was selected for embeddings to avoid requiring Google Cloud/Vertex AI setup.

Current embedding model:

```text
embeddinggemma
```

### Ollama

Ollama runs the embedding model locally.

```text
Text
 ↓
Ollama
 ↓
embeddinggemma
 ↓
Vector
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

### Test Endpoint

```text
GET http://localhost:8080/embedding?text=Java is a programming language
```

The application returns the number of dimensions in the generated vector.

### Key Concept

```text
Text
 ↓
Embedding Model
 ↓
Numerical Vector
```

---

# 🗄️ Lesson 6 — PostgreSQL + pgvector

Now the generated embeddings need to be stored somewhere.

For this project, **PostgreSQL + pgvector** is being used as the vector database.

## Why pgvector?

PostgreSQL normally stores relational data.

The `pgvector` extension allows PostgreSQL to store and search vector embeddings.

```text
PostgreSQL
     +
 pgvector
     ↓
Vector Database
```

---

## 🐳 Run PostgreSQL with Docker

The project uses the Docker image:

```text
pgvector/pgvector:pg17
```

The Windows host port is **5434** because port `5433` was already being used by an existing local PostgreSQL installation.

```powershell
docker run --name rag-postgres `
  -e POSTGRES_USER=postgres `
  -e POSTGRES_PASSWORD=postgres `
  -e POSTGRES_DB=ragdb `
  -p 5434:5432 `
  -d pgvector/pgvector:pg17
```

### Port Mapping

```text
Windows
5434
  ↓
Docker
5432
  ↓
PostgreSQL
```

Check the container:

```powershell
docker ps
```

Expected:

```text
0.0.0.0:5434->5432/tcp
```

---

## 🔌 Connect to PostgreSQL

```powershell
docker exec -it rag-postgres psql -U postgres -d ragdb
```

Create the pgvector extension:

```sql
CREATE EXTENSION IF NOT EXISTS vector;
```

Check installed extensions:

```sql
\dx
```

Exit:

```sql
\q
```

---

# 📦 Spring AI PGVector Dependency

Add:

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-vector-store-pgvector</artifactId>
</dependency>
```

PostgreSQL JDBC driver:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

# ⚙️ PostgreSQL Configuration

`application.properties`

```properties
spring.datasource.url=jdbc:postgresql://127.0.0.1:5434/ragdb
spring.datasource.username=postgres
spring.datasource.password=postgres

spring.ai.vectorstore.pgvector.initialize-schema=true
```

### Complete Provider Configuration

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://127.0.0.1:5434/ragdb
spring.datasource.username=postgres
spring.datasource.password=postgres

# PGVector
spring.ai.vectorstore.pgvector.initialize-schema=true

# Gemini - Chat Model
spring.ai.google.genai.api-key=${GEMINI_API_KEY}
spring.ai.google.genai.chat.options.model=gemini-3.6-flash

# Ollama - Embedding Model
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.model.embedding=ollama
spring.ai.ollama.embedding.options.model=embeddinggemma

# Chat Provider
spring.ai.model.chat=google-genai
```

---

# 💾 Store Documents in Vector Database

Spring AI provides the `VectorStore` abstraction.

### VectorStore Service

```java
@Service
public class VectorStoreService {

    private final VectorStore vectorStore;

    public VectorStoreService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void saveChunks(List<String> chunks) {

        List<Document> documents = chunks.stream()
                .map(Document::new)
                .toList();

        vectorStore.add(documents);
    }
}
```

### Controller

```java
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
```

### Test Request

```text
POST http://localhost:8080/vector/save
```

Body → raw → JSON:

```json
[
    "Java is a programming language.",
    "Spring Boot is used to build Java backend applications.",
    "Kafka is a distributed event streaming platform."
]
```

---

# 🔍 What Happens Internally?

When chunks are added to the `VectorStore`:

```text
Chunk
 ↓
Embedding Model
 ↓
Vector
 ↓
PostgreSQL + pgvector
```

Spring AI handles the interaction with the embedding model and vector store.

---

# 🧠 Current Architecture

At the end of Lesson 6, the project has learned these individual components:

```text
PDF
 ↓
Text Extraction
 ↓
Text Chunks
 ↓
Embeddings
 ↓
PostgreSQL + pgvector
```

The missing part is retrieving the most relevant chunks for a user's question.

---

# 🔜 Lesson 7 — Similarity Search

The next lesson will implement:

```text
User Question
      ↓
Question Embedding
      ↓
Vector Similarity Search
      ↓
pgvector
      ↓
Most Relevant Chunks
```

For example:

```text
Question:
"What is Kafka?"
        ↓
Embedding
        ↓
Search Vector Database
        ↓
Relevant Chunk:
"Kafka is a distributed event streaming platform..."
```

---

# 🎯 Lesson 8 — Complete RAG

Finally, the retrieved chunks will be sent to Gemini along with the user's question.

```text
User Question
      ↓
Embedding
      ↓
Similarity Search
      ↓
Relevant Documents
      ↓
Context + Question
      ↓
Gemini
      ↓
Final Answer
```

This is the complete **Retrieval-Augmented Generation (RAG)** pipeline.

---

# 🔐 Security

Never commit API keys or secrets.

`.gitignore`:

```gitignore
.env
target/
.idea/
*.iml
.vscode/
```

Keep:

```env
GEMINI_API_KEY=your_api_key
```

inside `.env` and never push it to GitHub.

---

# 📌 Key Concepts Learned

### LLM

A Large Language Model generates responses based on the input prompt.

### Embedding

Converts text into numerical vectors representing its meaning.

### Vector Database

Stores embeddings and allows similarity searches.

### pgvector

PostgreSQL extension for storing and searching vectors.

### RAG

Combines retrieval from external knowledge with LLM generation.

```text
Retrieve relevant information
             +
Generate answer using LLM
             =
             RAG
```

---

# 🏁 Current Status

**Completed:** Lessons 1–6 ✅

The application currently has:

* Spring Boot REST API
* Gemini LLM integration
* PDF text extraction
* Text chunking
* Ollama embeddings
* `embeddinggemma`
* PostgreSQL
* pgvector
* Spring AI VectorStore
* Docker-based PostgreSQL

**Next:** Similarity Search → Complete RAG 🚀

