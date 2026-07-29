# Smart Finance Manager - Spring Boot Backend

Complete Spring Boot backend for Smart Finance Manager with JWT authentication, user management, and MySQL integration.

---

## Project Info

| Property | Value |
|----------|-------|
| **Package** | `com.smart_finance_manager_backend` |
| **Main Class** | `SmartFinanceManagerBackendApplication` |
| **Port** | `9090` |
| **Database** | MySQL `smart_finance_db` |
| **Spring Boot** | `3.3.2` |
| **Java** | `17` |

---

## Project Structure

```
src/main/java/com/smart_finance_manager_backend/
├── SmartFinanceManagerBackendApplication.java
├── config/
│   └── SecurityConfig.java
├── controller/
│   └── AuthController.java
├── dto/
│   ├── ApiResponse.java
│   ├── AuthResponseDto.java
│   ├── LoginDto.java
│   └── RegisterDto.java
├── entity/
│   └── User.java
├── enums/
│   ├── Role.java
│   └── SubscriptionPlan.java
├── exception/
│   ├── BadRequestException.java
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   └── UnauthorizedException.java
├── repository/
│   └── UserRepository.java
├── security/
│   ├── CustomUserDetailsService.java
│   ├── JwtAuthenticationFilter.java
│   └── JwtUtil.java
└── service/
    └── UserService.java
```

---

## API Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/register` | Register new user | No |
| POST | `/api/auth/login` | Login user | No |

---

## Setup

### 1. Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### 2. Database
```sql
CREATE DATABASE smart_finance_db;
```

### 3. Configure
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.password=your_mysql_password
jwt.secret=your-256-bit-secret-key-must-be-at-least-32-characters-long
```

### 4. Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

Server runs at `http://localhost:9090`

---

## API Examples

### Register
```bash
curl -X POST http://localhost:9090/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "phoneNumber": "+1234567890",
    "password": "Password123!",
    "confirmPassword": "Password123!",
    "subscriptionPlan": "FREE"
  }'
```

### Login
```bash
curl -X POST http://localhost:9090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "Password123!"
  }'
```

---

## Response Format

```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "type": "Bearer",
    "id": 1,
    "fullName": "John Doe",
    "email": "john@example.com",
    "role": "USER",
    "subscriptionPlan": "FREE"
  },
  "timestamp": "2026-07-29T10:45:00"
}
```

---

## React Integration

```javascript
const API_BASE = 'http://localhost:9090/api';

// Login
const login = async (credentials) => {
  const res = await fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(credentials)
  });
  const data = await res.json();
  if (data.success) {
    localStorage.setItem('token', data.data.token);
    localStorage.setItem('user', JSON.stringify(data.data));
  }
  return data;
};

// Authenticated request
const fetchWithAuth = async (url, options = {}) => {
  const token = localStorage.getItem('token');
  return fetch(url, {
    ...options,
    headers: {
      ...options.headers,
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
};
```
