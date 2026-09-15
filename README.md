# 🤖 AI Document Q&A

Learning project to understand **Spring AI, Gemini, and RAG** using Spring Boot.

---

# 📘 Lesson 1 — Spring Boot Setup

### 🎯 Goal

Create a basic Spring Boot application and REST API.

### 🛠️ Technologies

* Java 21
* Spring Boot
* Maven
* Spring Web

### 📁 Project Structure

```text
ai-document-qa/
├── src/
├── pom.xml
└── README.md
```

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

### 🧠 What I learned

* Spring Boot basics
* REST Controller
* `@RestController`
* `@GetMapping`
* Maven
* Running a Spring Boot application

---

# 📘 Lesson 2 — Spring AI + Gemini

### 🎯 Goal

Connect Spring Boot with **Google Gemini** and get AI responses.

### 🔄 Architecture

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

Store the Gemini API key in `.env`:

```env
GEMINI_API_KEY=your_api_key
```

Never upload `.env` to GitHub.

`.gitignore`:

```gitignore
.env
target/
```

### ⚙️ Gemini Configuration

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

Simple meaning:

```text
prompt()  → send question
call()    → call Gemini
content() → get response
```

### 🌐 API

```text
GET /ai?question=What is Kafka?
```

### 🧠 What I learned

* LLM
* Google Gemini
* Spring AI
* API keys
* `.env`
* `ChatClient`
* Prompt
* Calling an LLM from Spring Boot

---

# 📘 Lesson 3 — PDF Text Extraction

### 🎯 Goal

Read a PDF and extract its text.

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

### 🔧 PDF Service

We use:

```java
PDFTextStripper
```

to extract text from the PDF.

Basic flow:

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

In Postman:

```text
Body
 ↓
form-data
 ↓
key = file
 ↓
type = File
 ↓
Select PDF
```

### 🧠 What I learned

* PDF upload using `MultipartFile`
* Apache PDFBox
* `PDDocument`
* `PDFTextStripper`
* Extracting text from PDF
* Creating a file upload REST API

---

# 🏗️ Current RAG Progress

```text
Lesson 1
Spring Boot
    ↓
Lesson 2
Gemini / LLM
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

## Lesson 4 — Text Chunking

We will learn how to split large PDF text into smaller **chunks** so that we can later search for relevant information.

