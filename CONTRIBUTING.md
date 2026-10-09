# Contributing to RibinaMart

Thank you for your interest in contributing to **RibinaMart**, an Anna University R2025 Semester 3 Computer Science Capstone project. This document outlines our development guidelines, branching workflows, coding standards, and testing procedures.

---

## 🛠 1. Development Prerequisites

- **JDK**: OpenJDK or Oracle JDK 17 LTS (Java 17 `--release 17` required)
- **Build Tool**: Apache Maven 3.8+
- **Version Control**: Git 2.30+
- **IDE**: IntelliJ IDEA, Eclipse, VS Code, or Antigravity

---

## 🚀 2. Local Setup & Quickstart

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Ribina472/RibinaMart.git
   cd RibinaMart
   ```

2. **Copy Environment Configuration**:
   ```bash
   cp .env.example .env
   ```

3. **Compile and Run Tests**:
   ```bash
   mvn clean test
   ```

4. **Launch Local Server**:
   ```bash
   mvn compile exec:java
   ```
   Access the web application at: `http://localhost:8085/ribinamart`

---

## 🌿 3. Git Branching & Commit Workflow

- `main`: Production-ready, stable codebase. Direct commits are restricted to releases and maintenance tags.
- `feature/<feature-name>`: Feature branches (e.g. `feature/wishlist`, `feature/ai-chatbot`).
- `bugfix/<issue-name>`: Hotfixes and bug remediations.

### Commit Message Guidelines (Conventional Commits)
Follow the standard format: `<type>(<scope>): <short description>`
- `feat(chatbot)`: Add GeminiChatProvider with fallback
- `feat(wishlist)`: Implement save-for-later controller and DAO
- `fix(auth)`: Sanitize redirect URLs in AuthFilter
- `test(order)`: Add unit tests for atomic checkout transaction
- `docs(final)`: Complete final academic project report and slide deck

---

## 📐 4. Architectural Rules & Coding Standards

1. **Layered Separation of Concerns**:
   - `controller`: HTTP request mapping, parameter validation, session reading, response redirection/forwarding.
   - `service`: Core business logic, transactional validations, authorization checks, computations.
   - `dao`: Database CRUD operations only. Must strictly use `PreparedStatement` with bind variables (`?`).
   - `model`: Plain Java entities with private fields, getters, setters, and constructors.
   - `dto`: Serializable Data Transfer Objects for API and presentation layers.
2. **Security Requirements**:
   - **SQL Injection**: 100% prohibited. Never concatenate strings into SQL queries.
   - **XSS**: Always escape user output using JSTL `<c:out value="..."/>` or HTML entity replacements.
   - **Authentication**: Passwords must always be hashed with `PasswordUtil.hashPassword()` (BCrypt, work factor 12).
   - **Authorization**: Restrict protected routes using `AuthFilter` and verify ownership of resources (e.g., sellers cannot edit another seller's products).
3. **Database Transactions**:
   - Multi-step operations (e.g. order creation with stock decrement and cart clearance) must use manual connection transactions (`conn.setAutoCommit(false)`, `conn.commit()`, and `conn.rollback()`).

---

## 🧪 5. Testing Guidelines

- Write unit tests for all new DAO and Service classes using **JUnit 5** and **Mockito**.
- Run the full suite before submitting pull requests:
  ```bash
  mvn test
  ```
- All pull requests must pass GitHub Actions CI without failures.

---

## 📋 6. Code Review Checklist

- [ ] All tests pass (`mvn clean test`).
- [ ] No hardcoded database credentials or API keys.
- [ ] Proper error handling and user-friendly error messages.
- [ ] Javadoc comments provided for public classes and service methods.
