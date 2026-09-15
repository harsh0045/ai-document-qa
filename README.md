# 🤖 AI Document Q&A

A learning project to understand **Spring AI, Google Gemini, and RAG** using Spring Boot.

---

# 📚 Lessons

## Lesson 1 — Spring Boot Setup

### 🎯 Goal

Create a basic Spring Boot application and REST API.

### 🛠️ Technologies

* Java 21
* Spring Boot
* Maven
* Spring Web

### 🚀 REST API

```java
@RestController
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello AI!";
    }
}
```

Test:

```text
http://localhost:8080/hello
```

### 🧠 Learned

* Spring Boot basics
* REST API
* `@RestController`
* `@GetMapping`
* Maven
* Project structure

---

# Lesson 2 — Spring AI + Gemini

### 🎯 Goal

Connect Spring Boot with **Google Gemini** and generate AI responses.

### 🔄 Flow

```text
Browser
   ↓
Spring Boot
   ↓
ChatClient
   ↓
Spring AI
   ↓
Gemini
   ↓
AI Response
```

### 📦 Dependency

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-google-genai</artifactId>
</dependency>
```

### 🔑 API Key

Store the API key in `.env`:

```env
GEMINI_API_KEY=your_api_key
```

Never upload `.env` to GitHub.

`.gitignore`:

```gitignore
.env
target/
```

### ⚙️ Configuration

```properties
spring.ai.google.genai.api-key=${GEMINI_API_KEY}
spring.ai.google.genai.chat.options.model=gemini-3.6-flash
```

### 🤖 ChatClient

```java
return chatClient
        .prompt(question)
        .call()
        .content();
```

Meaning:

```text
prompt()  → Send question
call()    → Call Gemini
content() → Get response
```

### 🌐 API

```text
GET /ai?question=What is Kafka?
```

### 🧠 Learned

* LLM
* Google Gemini
* Spring AI
* ChatClient
* Prompts
* API keys
* `.env`
* Calling an LLM from Spring Boot

---

# Lesson 3 — PDF Text Extraction

### 🎯 Goal

Upload a PDF and extract its text.

### 🔄 Flow

```text
PDF
 ↓
PDFBox
 ↓
Extract Text
 ↓
Plain Text
```

### 📦 Dependency

```xml
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>3.0.6</version>
</dependency>
```

### 🔧 Main Classes

```text
PDDocument
PDFTextStripper
```

Example:

```java
try (PDDocument document = Loader.loadPDF(file)) {

    PDFTextStripper stripper = new PDFTextStripper();

    return stripper.getText(document);
}
```

### 🌐 Upload API

```text
POST /documents/upload
```

Using Postman:

```text
Body
 ↓
form-data
 ↓
key = file
 ↓
Type = File
 ↓
Select PDF
```

### 🧠 Learned

* `MultipartFile`
* Apache PDFBox
* Reading PDF files
* Extracting text
* File upload REST API

---

# Lesson 4 — Text Chunking

### 🎯 Goal

Split large PDF text into smaller pieces called **chunks**.

### ❓ Why Chunking?

A large PDF may contain thousands of words. Instead of using the entire document, we divide it into smaller parts.

```text
PDF
 ↓
Extract Text
 ↓
┌─────────┐
│ Chunk 1 │
├─────────┤
│ Chunk 2 │
├─────────┤
│ Chunk 3 │
├─────────┤
│ Chunk 4 │
└─────────┘
```

### 🔧 TextChunker

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

### Example

If:

```text
Text = 5000 characters
Chunk size = 1000
```

Then:

```text
5000 characters
      ↓
Chunk 1 → 1000
Chunk 2 → 1000
Chunk 3 → 1000
Chunk 4 → 1000
Chunk 5 → 1000
```

### 🧠 Learned

* What is chunking
* Why chunking is needed in RAG
* Chunk size
* Splitting large text into smaller pieces
* Preparing documents for embeddings

---

# 🏗️ RAG Progress

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
Embeddings
     ↓
Lesson 6
Vector Database
     ↓
Lesson 7
RAG
```

---

# 🚀 Next Lesson

## Lesson 5 — Embeddings

We will learn:

```text
Text Chunk
    ↓
Embedding Model
    ↓
Vector
```

These vectors will allow our application to find **similar and relevant information** from the uploaded documents.

