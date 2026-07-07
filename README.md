# Music

Music is a Spring Boot music-sharing and interaction platform. It supports music upload/playback, user registration and email activation, likes, tree-structured comments, hot-score ranking, **real-time notification push (SSE)**, Kafka-based new-release recommendations, and dynamic system rule configuration.

This README covers both the overall architecture and per-module implementation, as well as environment setup and `curl` examples.

## Tech Stack

| Category | Technology |
| --- | --- |
| Core framework | Spring Boot 4.0.6 (Spring MVC) |
| Language / JDK | Java 17 |
| Persistence | MyBatis 4.0.1 + MySQL |
| Cache / KV | Redis (Lettuce client) |
| Message queue | Apache Kafka (spring-kafka) |
| Object storage | MinIO (audio files, cover images) |
| Mail | Spring Mail (QQ SMTP) |
| Real-time push | SSE (Server-Sent Events) |
| Utilities | Lombok |
| Build | Maven |

## Features

- **User management**: register, email activation, login, interest management, and user CRUD
- **Music core**: music CRUD plus audio/cover-image upload to MinIO
- **Like system**: like/unlike a song with bidirectional queries (Redis-only)
- **Hot ranking**: maintain hot scores with a Redis ZSet and query the Top N songs
- **Comments**: unlimited-depth tree-structured comments with cascade delete
- **Notifications**: in-app notifications with **real-time SSE push** to the browser
- **Play records**: report playback and sync to an external system
- **Messaging & recommendation**: Kafka new-release events and tag-based recommendation emails
- **System configuration**: dynamic `code`-`value` business-rule configuration

The layered call flow is uniform: `Controller → Service → (Mapper for the database / Repository for Redis)`. Responses are wrapped in a common `BaseVO` (containing `code`, `success`, `duration`, and `message`).

## Project Structure

```text
src/main/java/com/example/music
├── MusicApplication.java          # Application entry point
├── config                         # Kafka / Redis / MinIO / RestTemplate / WebMvc configuration
├── consumer                       # Kafka consumers
├── controller                     # REST controllers (HTTP layer)
│   ├── cmd                        # Request command objects (input)
│   ├── converter                  # Entity-to-VO converters
│   └── vo                         # View objects (output, all include BaseVO)
├── entity                         # Database entity classes
├── enums                          # Enums
├── exception                      # Custom exceptions
├── intergration                   # External-system integration (email, cross-system sync)
├── mapper                         # MyBatis mapper interfaces (XML in resources/mapper)
├── producer                       # Kafka producers
├── repository                     # Redis / cache access layer
├── service                        # Service interfaces
├── service/impl                   # Service implementations
└── util                           # MD5, activation code, and MinIO utilities

src/main/resources
├── application.properties         # Application configuration
├── file/INIT.sql                  # Database schema
├── mapper                         # MyBatis XML mapper files
└── static                         # Static frontend pages (HTML/JS)
```

## Requirements

Before running, prepare four middleware services plus a working mail account:

- JDK 17+
- Maven 3.8+, or the included Maven wrapper
- MySQL 8.x (schema in `src/main/resources/file/INIT.sql`)
- Redis
- Kafka
- MinIO

## Key Configuration (`application.properties`)

| Item | Value |
| --- | --- |
| Server port | `8082` |
| Database | `jdbc:mysql://localhost:3306/music` |
| Redis | `127.0.0.1:6379` |
| Kafka | `localhost:9092` |
| MinIO | `http://127.0.0.1:9000`, bucket `music` |
| Mail | `smtp.qq.com:465` (SSL) |
| Local audio directory | `./uploads/audio/`, mapped to URL `/audio/**` |
| Upload size limit | 50 MB per file |
| Logs | `./logs/springboot-music/`, rotated daily, kept for 30 days |

## Getting Started

1. Clone the repository:

```bash
git clone https://github.com/jiangzi33/music.git
cd music
```

2. Create the MySQL database and load the schema:

```sql
CREATE DATABASE music DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Then import `src/main/resources/file/INIT.sql`.

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

4. Start MySQL, Redis, Kafka, and MinIO.

5. Run the application:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The application runs by default at `http://localhost:8082`. Open `http://localhost:8082/login.html` in a browser.

## Database Tables

The schema is defined in `src/main/resources/file/INIT.sql`.

| Table | Description | Key fields |
| --- | --- | --- |
| `user` | Users | `name` (unique), `password` (MD5), `email` (unique), `status`, `interests`, `register_time` |
| `music` | Music | `title`, `content` (lyrics), `author`, `tags` (comma-separated), `picture_url`, `publish_time` |
| `comment` | Comments (tree) | `user_id`, `music_id`, `parent_id`, `content`, `leaf`, `create_time` |
| `notification` | Notifications | `from`, `to`, `target_type`, `target_id`, `operation`, `content`, `operation_time` |
| `system_config` | System rule config | `code` (unique), `value` |

> `music`, `user`, `notification`, and `system_config` DDL live in `INIT.sql`; the `comment` table structure can be inferred from the field mapping in `CommentMapper.xml`. Runtime-only fields such as audio/cover URLs and hot scores appear in the entities/VOs. Like relationships, user tokens, activation codes, and the ranking are stored in Redis.

## Modules

### 1. User Module (`/user`)

Handles the full account lifecycle: register → email activation → login → profile management.

| Method | Path | Description |
| --- | --- | --- |
| POST | `/user/register` | Register (`RegisterCmd`: name/password/age/interests/email) |
| PUT | `/user/activate?name=&code=` | Verify email activation code |
| GET | `/user/login?name=&password=` | Log in |
| PUT | `/user/update?userId=&interests=` | Update interests |
| GET | `/user/info?name=` / `/user/id?id=` | Query a user |
| GET | `/user/query-all?start=&pageSize=` | List users with pagination |
| POST/PUT/DELETE | `/user/add` `/user/modify` `/user/delete` | Admin create/update/delete |

**Core flows**

- **Register**: check username uniqueness → encrypt password with `MD5Util.md5()` and set status to `INIT` → persist → generate a 6-digit activation code (`ActivateUtil`) → send the activation email via the `MusicConstant.EMAIL_HTML` template (`{{code}}` placeholder) → store the code in Redis `user_activate:{name}` (**TTL 5 minutes**) → record the user's interest tags.
- **Activate**: read the code from Redis and compare; on success set status to `NORMAL`. Already-activated users return directly; `ABNORMAL` users are rejected.
- **Login**: validate status and MD5 password → generate a `UserToken` stored in Redis `user_token:{userId}` (**TTL 60 minutes**).

**Redis usage**

| Key | Content | TTL |
| --- | --- | --- |
| `user_name:{name}` / `user_id:{id}` | User object cache | Permanent (cleared on update/delete) |
| `user_token:{userId}` | Login token | 60 minutes |
| `user_activate:{name}` | Activation code | 5 minutes |

**User status enum** (`UserStatusEnum`): `INIT` (pending activation) → `NORMAL` (active); `ABNORMAL` (disabled — cannot activate or log in).

**Exceptions**: `UserDuplicatedRegisterException`, `UserNotExistException`, `UserPasswordErrorException`, `UserFailActivatedException`, `UserNotAllowedException`, `EmailFailActivatedException`.

> Security note: passwords use unsalted MD5, which is suitable for demos only. For production, switch to a salted hash such as BCrypt.

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

### 2. Music Core Module (`/music`)

Music CRUD plus audio/cover-image upload to MinIO.

| Method | Path | Description |
| --- | --- | --- |
| POST | `/music/upload/audio` | Upload audio to MinIO and return the access URL |
| POST | `/music/upload/image` | Upload a cover image to MinIO |
| POST | `/music/add` | Create music (`MusicCmd`) |
| PUT | `/music/modify` | Update music |
| DELETE | `/music/delete` | Delete music |
| GET | `/music/id?id=` | Query music by ID |
| GET | `/music/title?title=` | Query music by title |
| GET | `/music/query-all?start=&pageSize=` | Paginate (newest first) |

**File upload**: `MinioUtil` automatically creates the bucket and sets a public-read policy at startup. On upload, the object name is `{folder}/{uuid}/{original-filename}` (UUID prevents name collisions and path traversal), and a full directly-accessible URL is returned.

**Query cache**: queries by id/title go through Redis (`music_id:{id}`, `music_title:{title}`); on a miss they fall back to the database and refill the cache. Both keys are cleared on update/delete. Paginated queries are not cached.

**Entity fields**: `title`, `content` (lyrics), `author`, `tags`, `pictureUrl`, `audioUrl`, `publishTime`; `MusicVO` additionally carries `hotScore` (from the ranking).

**Linkage**: adding music initializes a ranking entry and publishes a new-release event via Kafka (see module 8).

Add music example:

```bash
curl -X POST http://localhost:8082/music/add \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Song Title",
    "content": "lyrics",
    "author": "Singer",
    "tags": "pop"
  }'
```

Query music example:

```bash
curl "http://localhost:8082/music/title?title=Song%20Title"
```

### 3. Like Module (`/music-like`)

Bidirectional like relationships implemented purely in Redis.

| Method | Path | Description |
| --- | --- | --- |
| POST | `/music-like/add` | Like a song |
| PUT | `/music-like/cancel` | Cancel a like |
| GET | `/music-like/get-music-like?musicId=` | Users who liked a song |
| GET | `/music-like/get-user-like?userId=` | Songs liked by a user |

**Storage**: Redis Lists maintained on both sides — `music_like:{musicId}` (liking users) and `user_like:{userId}` (liked songs). Liking `rightPush`es to both; canceling `remove`s from both. The hot score is updated in sync (`+1.0` / `-1.0`).

Examples:

```bash
curl -X POST "http://localhost:8082/music-like/add?musicId=1&userId=1"
curl "http://localhost:8082/music-like/get-user-like?userId=1"
```

### 4. Hot Ranking Module (`/music-rank`)

| Method | Path | Description |
| --- | --- | --- |
| GET | `/music-rank/topN?n=` | Get the Top N songs by hot score |

**Storage**: a Redis Sorted Set under the single key `music_rank` (member = musicId, score = hot score).

**Scoring rules** (`MusicConstant`): like `LIKE_SCORE=1.0`, comment `COMMENT_SCORE=2.0`. Queries use `reverseRange` to get IDs in descending order, then `MusicMapper.queryByIds` batch-loads full records.

Example:

```bash
curl "http://localhost:8082/music-rank/topN?n=10"
```

### 5. Comment Module (`/comment`)

Unlimited-depth tree-structured comments (`parent_id` links; `leaf` marks whether a node is a leaf).

| Method | Path | Description |
| --- | --- | --- |
| POST | `/comment/add` | Post a comment / reply |
| GET | `/comment/query?musicId=` | Query all comments of a song |
| GET | `/comment/get?id=` | Query a single comment by ID (used for notification jump/locate) |
| PUT | `/comment/modify?id=&content=` | Edit a comment |
| DELETE | `/comment/delete?id=` | Delete a comment (cascades to descendants) |

**Key points**:

- Posting a comment adds `COMMENT_SCORE` to the song's hot score. If it is a reply (`parentId ≠ 0`), the parent comment is marked non-leaf and **a notification is generated for the parent comment's author**.
- Deleting a comment uses a BFS queue to cascade-delete all descendant comments and deducts the hot score by the number deleted.
- On query, the backend `fillUserNames` batch-fills commenters' display names (cached to avoid N+1 queries).

### 6. Notification Module (`/notification`) — includes real-time push

In-app notifications delivered in real time via **SSE (no refresh needed)**.

| Method | Path | Description |
| --- | --- | --- |
| GET | `/notification/query?to=&start=&size=` | Paginate notifications received by a user |
| GET | `/notification/subscribe?to=` | **Subscribe to the real-time notification stream** (`text/event-stream`) |
| DELETE | `/notification/delete?id=` | Delete a notification |

**Real-time push design**

- `NotificationEmitterRegistry` (`@Component`) maintains an `SseEmitter` connection pool per user (`Map<userId, List<SseEmitter>>`, supporting multiple tabs for one user). Connections time out after 30 minutes; errors/timeouts are cleaned up automatically, and the browser `EventSource` reconnects on disconnect.
- After `NotificationServiceImpl.addNotification` writes to the database, it immediately pushes a `notification` event to the recipient via the registry. The mapper uses `useGeneratedKeys` to backfill the auto-increment id so the push payload carries an id for frontend dedup / unread marking.
- **Frontend behavior**:
  - `music.html` opens an SSE connection and recomputes the bell's unread badge on each push (based on the `localStorage` read cursor `notifRead_{userId}`), with no refresh.
  - `notification.html` inserts new notifications at the top of the list and highlights them as unread on push.
  - Clicking a notification (`targetType=COMMENT`) navigates to `music.html?commentId=`; the page reverse-looks-up the owning song by comment id → opens the detail → locates and highlights the comment (`scrollIntoView` + flash animation).

### 7. Play Records & Cross-System Sync (`/play`)

| Method | Path | Description |
| --- | --- | --- |
| POST | `/play/play` | Report a play record (`PlayRecordCmd`: userId/soundId/duration) |

`SyncIntegration` uses `RestTemplate` (injected by `RestConfig`) to POST to the external system at `http://127.0.0.1:8083/record/sync`. A non-200 response or failure throws `AcrossSysException`.

### 8. Messaging & Recommendation (Kafka)

- **MusicProducer**: publishes the musicId to topic `add-music` when new music is added.
- **MusicConsumer** (consumer group `test-group`, listening on `add-music`):
  1. updates the ranking entry;
  2. retrieves target users by the music's tags from `MusicInterestsRepository` (Redis Set `recommend_by_music_tag:{tag}`, holding "the set of users interested in a given tag");
  3. sends a new-release recommendation email to those users using the `MusicConstant.RECOMMEND_EMAIL_HTML` template.
- **TestProducer / TestConsumer**: send/receive sample on `test-topic` (paired with the `/test-message` endpoint).

> This module decouples "new-release notification/recommendation" from the main flow asynchronously, preventing the upload endpoint from being blocked by email sending.

Kafka test example:

```bash
curl -X POST "http://localhost:8082/test-message/sendMessage?topic=test-topic&message=hello"
```

### 9. System Configuration Module (`/systemConfig`)

Dynamically maintains business rules (e.g. page size) as `code`-`value` pairs to avoid hardcoding.

| Method | Path | Description |
| --- | --- | --- |
| POST | `/systemConfig/add` | Add a config item (`SystemConfigCmd`) |
| GET | `/systemConfig/modify?code=&value=` | Update a config value |
| GET | `/systemConfig/query?code=` | Query a single item |
| GET | `/systemConfig/queryAll` | Query all items |
| DELETE | `/systemConfig/delete?code=` | Delete an item |

`code` is unique; dotted naming is recommended (e.g. `rule.search.pageSize`). The visual management page is `rule-config.html`.

Add configuration example:

```bash
curl -X POST http://localhost:8082/systemConfig/add \
  -H "Content-Type: application/json" \
  -d '{
    "code": "site_name",
    "value": "Music"
  }'
```

## Frontend Pages (`resources/static`)

Plain static HTML/JS that calls the backend via `fetch`.

| Page | Purpose |
| --- | --- |
| `login.html` / `register.html` / `activate.html` | Login / register / email activation |
| `music.html` | Home: search, music list and detail, playback, likes, comments, notification bell (live badge) |
| `notification.html` | Notification center (real-time SSE, click to jump to and locate a comment) |
| `rank.html` | Hot ranking |
| `user.html` | User profile management |
| `rule-config.html` | System rule configuration management |

## Infrastructure Configuration

| Config class | Purpose |
| --- | --- |
| `RedisConfig` | Lettuce connection; keys use `StringRedisSerializer`, values use `GenericJackson2JsonRedisSerializer` (JSON) |
| `MinioConfig` | Builds `MinioClient` (endpoint/credentials/bucket read from config) |
| `KafkaConfig` | Producer/consumer serialization, consumer group, offset policy |
| `RestConfig` | Provides the `RestTemplate` bean |
| `WebMvcConfig` | Maps the local `./uploads/audio/` directory to the `/audio/**` static resource path |

## Response Format

Most APIs return a common `BaseVO` structure containing the status code, success flag, request cost, and message:

```json
{
  "code": 200,
  "success": true,
  "duration": 12,
  "message": null
}
```

Query APIs return additional user, music, comment, notification, or system-configuration data together with the base response.

## Redis Data

| Key | Content |
| --- | --- |
| `music_like:{musicId}` | List of user IDs who liked a song |
| `user_like:{userId}` | List of music IDs liked by a user |
| `music_rank` | ZSet used for music hot-score ranking |
| `recommend_by_music_tag:{tag}` | Set of users interested in a given tag |
| `music_id:{id}` / `music_title:{title}` | Music object cache |
| `user_name:{name}` / `user_id:{id}` | User object cache |
| `user_token:{userId}` | Login token (60 min) |
| `user_activate:{name}` | Activation code (5 min) |

## Typical Call Chains

**Posting a reply triggers a real-time notification**

```text
POST /comment/add (parentId ≠ 0)
  └ CommentServiceImpl.addComment
      ├ commentMapper.addComment              # persist
      ├ musicRankRepository.updateScore(+2)   # hot score
      └ notificationService.addNotification
          ├ notificationMapper.addNotification (backfill id)
          └ NotificationEmitterRegistry.send(toUserId, VO)  # SSE push
                └ recipient's browser EventSource receives → badge +1 / list top (no refresh)
```

**Query the ranking**

```text
GET /music-rank/topN?n=10
  └ ZREVRANGE music_rank 0 9 → [musicId...] → MusicMapper.queryByIds → MultiMusicVO
```

**New-release recommendation**

```text
POST /music/add → Kafka(add-music, musicId)
  └ MusicConsumer consumes
      ├ register in the ranking
      └ match interested users by tags → send recommendation emails one by one
```

## Notes

- User registration sends an email activation code, so SMTP settings must be configured before testing registration.
- User passwords are stored after MD5 hashing (demo only; use a salted hash in production).
- Newly added music is automatically added to the Redis ranking set.
- Liking a song increases its hot score, and canceling a like decreases it.
- The Kafka property is currently written as `spring.kafka.bootstrap-services`. If Kafka cannot connect, check whether it should be changed to the standard Spring Boot property `spring.kafka.bootstrap-servers`.
- The project does not currently include authentication interceptors. Before production use, consider adding login-state validation, request validation, centralized exception handling, and secret isolation.

## Tests

Run tests with:

```bash
./mvnw test
```

If tests start the Spring application context, make sure MySQL, Redis, Kafka, and MinIO are running and correctly configured.