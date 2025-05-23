
# Notification Service - Spring Boot Microservice

A Spring Boot-based microservice for managing and delivering notifications to users and groups within a multi-tenant system.

---

## 📖 Overview

This service provides a RESTful API for:

- Creating and sending notifications to individual users or groups
- Retrieving notifications for a user or a group
- Marking notifications as read or unread
- Viewing all notifications as an admin

## 📂 Project Structure

```
src/main/java/com/thathsara/notification_service/
├── config
│   ├── checkstyle
│   ├── pmd
│   ├── spotbugs
│   └── SecurityConfig.java
├── controller
│   └── AuthController.java
├── dto
│   └── (All DTO classes)
├── exception
│   └── (All Exception classes)
├── middleware
│   └── (All middleware classes)
├── entity
│   └── (All models classes)
├── repository
│   └── (All repository classes)
├── service
│   └── (All service classes)
├── utils
│   └── (All utilities classes)
└── AuthServiceApplication.java
```

## 🚀 API Endpoints

### 📤 Send Notification

**POST** `/api/v1/notification/`

Send a new notification to users or a group.

**Headers:**

- `Tenant-Id`: Long

**Request (Form Data):**

- `title`
- `message`
- `userIds` (optional)
- `groupName` (optional)

**Response:**

- `notificationId`
- `message`

---

### 📥 Get User Notifications

**GET** `/api/v1/notification/user/{userid}/notifications`

Retrieve notifications for a user.

**Query Params:** `page`, `limit`

**Headers:** `Tenant-Id`

---

### 📥 Get Group Notifications

**GET** `/api/v1/notification/user/{groupname}/notifications`

Retrieve notifications for a group.

**Query Params:** `page`, `limit`

**Headers:** `Tenant-Id`

---

### 📥 Get Group Notifications

**GET** `/api/v1/notification/user/{groupname}/notifications`

Retrieve notifications for a group.

**Query Params:** `page`, `limit`

**Headers:** `Tenant-Id`

---
### ✅ Mark Group Notification as Read/Unread

**PUT** `/api/v1/notification/group/{groupName}/user/{userid}/notification/{notificationid}/read`

**PUT** `/api/v1/notification/group/{groupName}/user/{userid}/notification/{notificationid}/unread`

**Headers:** `Tenant-Id`

---
### 📑 Get All Notifications (Admin)

**GET** `/api/v1/notification/`

Retrieve all notifications in a tenant.

**Query Params:** `page`, `limit`

**Headers:** `Tenant-Id`

---

## 📦 Tech Stack

- Java 24
- Spring Boot
- Spring Web
- Spring Security
- JWT (JSON Web Tokens)
- Redis (Optional, for caching tokens/OTPs)
- MySQL (or preferred RDBMS)
- Maven

## ⚙️ How to Run

1. Clone the repository:
```
git clone https://github.com/thathsarabandara/stormgate-auth-service.git
cd auth-service
```

2. Configure `application.properties` for your database, JWT secret, and SMTP email service.

3. Build and run:
```
./mvnw spring-boot:run
```

## 🤝 Contribution
### Contributions are welcome!

1. Fork this repository

2. Create a new branch git checkout -b feature/your-feature

3. Commit your changes git commit -m 'Add your feature'

4. Push to the branch git push origin feature/your-feature

5. Open a Pull Request

## 📞 Contact
### Thathsara Bandara
- 📧 [thathsaraBandara.dev](https://portfolio-v1-topaz-ten.vercel.app/)
- 🌐 [LinkedIn - Thathsara Bandara](https://www.linkedin.com/in/thathsara-bandara-b403582a7/)
- 💻 [Github](https://www.linkedin.com/in/thathsara-bandara-b403582a7/)
- ✉️ [Contant Developer](mailto:thathsaraarumapperuma@gmail.com?subject=Auth%20Service%20Support&body=Hello,%20I%20need%20help%20with...)

## 📄 License

This project is licensed under the MIT License.

Copyright (c) 2025 Thathsara Bandara

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

---

© 2025 Thathsara Bandara
