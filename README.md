# BFH Java Qualifier – Spring Boot App

This is my submission for the **Bajaj Finserv Health | Qualifier 1 | JAVA** challenge.  
It is a Spring Boot application that:

- On startup, sends a POST request to generate a webhook (with my details).
- Solves the SQL problem (Question 1, since my regNo ends with odd digits).
- Saves the final SQL query to a local file (`final-query.sql`).
- Submits the SQL query to the provided webhook endpoint using a **JWT token**.

---

## 🚀 Tech Stack
- Java 17
- Spring Boot 3
- WebClient (for REST calls)
- Maven (build tool)
