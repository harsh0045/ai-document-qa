# 🤖 AI Document Q&A

Learning project to understand **Spring AI, LLMs, Gemini, and RAG** using Spring Boot.

---

# 📘 Lesson 1 — Spring Boot Setup

## 🎯 Goal

Create a basic Spring Boot application and understand the project structure.

## 🛠️ Technologies

* Java 21
* Spring Boot
* Maven
* Spring Web

## 📁 Basic Structure

```text
ai-document-qa/
├── src/
│   └── main/
│       ├── java/
│       └── resources/
├── pom.xml
└── README.md
```

## 🚀 Create a REST API

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

### What I learned

* Spring Boot project structure
* REST Controller
* `@RestController`
* `@GetMapping`
* Maven dependencies
* Running a Spring Boot application

---

# 📘 Lesson 2 — Spring AI + Gemini

## 🎯 Goal

Connect Spring Boot with **Google Gemini** and generate AI responses.

## 🔄 Architecture

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

## 📦 Main Dependency

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-google-genai</artifactId>
</dependency>
```

## 🔑 API Key

Store the Gemini API key in `.env`:

```env
GEMINI_API_KEY=your_api_key
```

Never upload `.env` to GitHub.

Add to `.gitignore`:

```gitignore
.env
target/
```

## ⚙️ Configuration

```properties
spring.ai.google.genai.api-key=${GEMINI_API_KEY}
spring.ai.google.genai.chat.options.model=gemini-3.6-flash
```

## 🤖 ChatClient

```java
private final ChatClient chatClient;

public AiController(ChatClient.Builder builder) {
    this.chatClient = builder.build();
}
```

Send a question:

```java
return chatClient
        .prompt(question)
        .call()
        .content();
```

### Meaning

```text
prompt() → send question
call()   → call Gemini
content() → get AI response
```

## 🌐 API

```text
GET /ai?question=What is Kafka?
```

Example:

```text
http://localhost:8080/ai?question=What%20is%20Kafka%3F
```

## 🧠 What I learned

* What is an LLM
* What is Gemini
* What is Spring AI
* API keys and `.env`
* `ChatClient`
* Prompts
* Calling an LLM from Spring Boot

---

# 🚀 Next: Lesson 3

## RAG — Document Processing

We will learn:

```text
PDF
 ↓
Extract Text
 ↓
Split into Chunks
 ↓
Embeddings
 ↓
Vector Database
```

This will be the foundation of our **AI Document Q&A** application.

