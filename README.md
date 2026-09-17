# ShortLink

## Project purpose

ShortLink converts a long HTTP/HTTPS URL into a six-character short code, stores the mapping in MySQL, and redirects the short URL to the original URL.

## Technologies used

Java 17, Spring Boot, Spring Web, Spring Data JPA/Hibernate, MySQL, Maven, and Bean Validation.

## Architecture

Client/Postman → Controller → Service → Repository → Spring Data JPA / Hibernate → MySQL

## Database structure

Database: `shortlink_db`  
Table: `urls` (`id`, `original_url`, `short_code`, `created_at`, `click_count`)

## API endpoints

| Method | Endpoint | Purpose |
| --- | --- | --- |
| POST | `/api/urls` | Create a short URL |
| GET | `/{shortCode}` | Redirect to the original URL and increment clicks |
| GET | `/api/urls/{shortCode}` | Get URL statistics |

## How to configure MySQL

Create the database:

```sql
CREATE DATABASE shortlink_db;
```

Set `DB_USERNAME` and `DB_PASSWORD` environment variables as needed. Optionally set `DB_URL`; it defaults to `jdbc:mysql://localhost:3306/shortlink_db`.

## How to run the application

```bash
mvn spring-boot:run
```

The application runs on `http://localhost:8080`.

## Example API requests/responses

Create a URL:

```http
POST /api/urls
Content-Type: application/json

{"originalUrl":"https://www.example.com/very/long/url"}
```

```json
{"shortCode":"aB72xK","shortUrl":"http://localhost:8080/aB72xK","originalUrl":null,"clickCount":null}
```

Redirect:

```http
GET /aB72xK
```

Returns `302 Found` with `Location: https://www.example.com/very/long/url`.

Get statistics:

```http
GET /api/urls/aB72xK
```

```json
{"shortCode":"aB72xK","shortUrl":null,"originalUrl":"https://www.example.com/very/long/url","clickCount":1}
```
