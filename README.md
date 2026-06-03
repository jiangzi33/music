# Music

Music is a Spring Boot backend project for managing users and music records. It provides REST APIs for user registration and activation, login, music management, likes, rankings, system configuration, and Kafka message testing.

## Tech Stack

- Java 17
- Spring Boot 4.0.6
- Spring Web MVC
- MyBatis
- MySQL
- Redis
- Kafka
- Spring Mail
- Maven

## Features

- User management: register, email activation, login, and delete users
- Music management: add, update, query, and delete music records
- Like system: like or unlike music, query users who liked a song, and query songs liked by a user
- Music ranking: maintain hot scores with Redis ZSet and query Top N songs
- System configuration: add, update, query, and delete configuration items
- Kafka test endpoint: send test messages to a specified Kafka topic

## Project Structure

```text
src/main/java/com/example/music
├── MusicApplication.java          # Application entry point
├── config                         # Redis and Kafka configuration
├── consumer                       # Kafka consumer
├── controller                     # REST controllers
├── controller/cmd                 # Request command objects
├── controller/converter           # Entity-to-VO converters
├── controller/vo                  # Response view objects
├── entity                         # Entity classes
├── enums                          # Enums
├── exception                      # Custom exceptions
├── intergration                   # Email utility
├── mapper                         # MyBatis mapper interfaces
├── producer                       # Kafka producer
├── repository                     # Redis repositories and cache logic
├── service                        # Service interfaces
├── service/impl                   # Service implementations
└── util                           # Utility classes

src/main/resources
├── application.properties         # Application configuration
└── mapper                         # MyBatis XML mapper files
```

## Requirements

- JDK 17+
- Maven 3.8+, or the included Maven wrapper
- MySQL 8.x
- Redis
- Kafka

## Getting Started

1. Clone the repository:

```bash
git clone https://github.com/jiangzi33/music.git
cd music
```

2. Create the MySQL database:

```sql
CREATE DATABASE music DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. Update the configuration:

Edit `src/main/resources/application.properties` and adjust the following values for your local environment:

```properties
server.port=8082
spring.datasource.url=jdbc:mysql://localhost:3306/music?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.username=your_database_username
spring.datasource.password=your_database_password

spring.mail.host=smtp.qq.com
spring.mail.port=465
spring.mail.username=your_email_account
spring.mail.password=your_email_authorization_code

spring.kafka.bootstrap-services=localhost:9092
```

Do not commit real database passwords, email authorization codes, or other secrets to the repository. Use environment variables or a local private configuration file for sensitive values.

4. Start MySQL, Redis, and Kafka.

5. Run the application:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The application runs by default at:

```text
http://localhost:8082
```

## Database Tables

The project does not currently include a standalone SQL schema file. Based on the entity classes and MyBatis mappings, you can use the following table definitions as a reference:

```sql
CREATE TABLE user (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  age INT,
  interests VARCHAR(255),
  email VARCHAR(255),
  status VARCHAR(50),
  register_time DATETIME
);

CREATE TABLE music (
  id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  content TEXT,
  author VARCHAR(255),
  tags VARCHAR(255),
  publish_time DATETIME
);

CREATE TABLE system_config (
  id INT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(100) NOT NULL UNIQUE,
  value VARCHAR(255)
);
```

Like relationships, user tokens, activation codes, and the music ranking are mainly stored in Redis.

## API Reference

### User APIs

| Method | Path | Description |
| --- | --- | --- |
| POST | `/user/register` | Register a user and send an email activation code |
| PUT | `/user/activate` | Activate a user |
| GET | `/user/login` | Log in |
| DELETE | `/user/delete` | Delete a user |

Register example:

```bash
curl -X POST http://localhost:8082/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "test",
    "password": "123456",
    "age": 18,
    "interests": "pop,rock",
    "email": "test@example.com"
  }'
```

Login example:

```bash
curl "http://localhost:8082/user/login?name=test&password=123456"
```

### Music APIs

| Method | Path | Description |
| --- | --- | --- |
| POST | `/music/add` | Add a music record |
| PUT | `/music/modify` | Update a music record |
| GET | `/music/id` | Query music by ID |
| GET | `/music/title` | Query music by title |
| DELETE | `/music/delete` | Delete music |

Add music example:

```bash
curl -X POST http://localhost:8082/music/add \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Song Title",
    "content": "song content or url",
    "author": "Singer",
    "tags": "pop"
  }'
```

Query music example:

```bash
curl "http://localhost:8082/music/title?title=Song%20Title"
```

### Like APIs

| Method | Path | Description |
| --- | --- | --- |
| POST | `/music-like/add` | Like a song |
| PUT | `/music-like/cancel` | Cancel a like |
| GET | `/music-like/get-music-like` | Query users who liked a song |
| GET | `/music-like/get-user-like` | Query songs liked by a user |

Examples:

```bash
curl -X POST "http://localhost:8082/music-like/add?musicId=1&userId=1"
curl "http://localhost:8082/music-like/get-user-like?userId=1"
```

### Ranking API

| Method | Path | Description |
| --- | --- | --- |
| GET | `/music-rank/topN` | Query the Top N songs by hot score |

Example:

```bash
curl "http://localhost:8082/music-rank/topN?n=10"
```

### System Configuration APIs

| Method | Path | Description |
| --- | --- | --- |
| POST | `/systemConfig/add` | Add a configuration item |
| GET | `/systemConfig/modify` | Update a configuration item |
| GET | `/systemConfig/query` | Query a configuration item |
| DELETE | `/systemConfig/delete` | Delete a configuration item |

Add configuration example:

```bash
curl -X POST http://localhost:8082/systemConfig/add \
  -H "Content-Type: application/json" \
  -d '{
    "code": "site_name",
    "value": "Music"
  }'
```

### Kafka Test API

| Method | Path | Description |
| --- | --- | --- |
| POST | `/test-message/sendMessage` | Send a test message to a specified Kafka topic |

Example:

```bash
curl -X POST "http://localhost:8082/test-message/sendMessage?topic=test-topic&message=hello"
```

## Response Format

Most APIs return a common `BaseVO` structure containing the status code, success flag, request cost, and message:

```json
{
  "code": 200,
  "success": true,
  "cost": 12,
  "message": null
}
```

Query APIs return additional user, music, or system configuration data together with the base response.

## Redis Data

The project stores part of its business data in Redis:

- `music_like:{musicId}`: list of user IDs who liked a song
- `user_like:{userId}`: list of music IDs liked by a user
- `music_rank`: ZSet used for music hot-score ranking
- Activation codes, user tokens, user cache, and music cache are managed by their corresponding repositories

## Notes

- User registration sends an email activation code, so SMTP settings must be configured before testing registration.
- User passwords are stored after MD5 hashing.
- Newly added music is automatically added to the Redis ranking set.
- Liking a song increases its hot score, and canceling a like decreases it.
- The Kafka property is currently written as `spring.kafka.bootstrap-services`. If Kafka cannot connect, check whether it should be changed to the common Spring Boot property `spring.kafka.bootstrap-servers`.
- The current project does not include authentication interceptors. Before using it in production, consider adding login-state validation, request validation, centralized exception handling, and secret isolation.

## Tests

Run tests with:

```bash
./mvnw test
```

If tests start the Spring application context, make sure MySQL, Redis, and Kafka are running and correctly configured.
