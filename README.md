# 📚 Book Library Management System

## Project Information

**Course:** Database Systems / Web Application Development  
**Institution:** Posts and Telecommunications Institute of Technology (PTIT)  
**Academic Year:** 2025-2026, Semester 1  
**Project Type:** Full-Stack Web Application  

---

## Executive Summary

This project presents a comprehensive **Book Library Management System** built as a full-stack web application. The system implements a complete digital library solution that enables users to upload, organize, read, and manage electronic books through a modern web interface. The application demonstrates advanced database design principles, secure authentication mechanisms, RESTful API architecture, and responsive user interface design.

**Key Capabilities:**
- Secure multi-user book library management with JWT-based authentication
- In-browser PDF reading with bookmark and annotation support
- Advanced search and filtering across multiple metadata fields
- Collection-based organization with many-to-many relationships
- Real-time progress tracking and rating system
- Responsive web interface with modern UX patterns

## System Architecture

### Technology Stack

#### Backend Framework
| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 LTS | Core programming language |
| Spring Boot | 3.2.0 | Application framework and dependency injection |
| Spring Web | 3.2.0 | RESTful API development |
| Spring Security | 6.2.0 | Authentication and authorization |
| Spring JDBC | 6.1.0 | Database connectivity layer |
| MySQL Connector/J | 8.2.0 | MySQL database driver |

#### Security & Authentication
| Technology | Version | Purpose |
|------------|---------|---------|
| JWT (jjwt) | 0.12.3 | Stateless token-based authentication |
| BCrypt (jbcrypt) | 0.4 | Password hashing with salt |
| Spring Security | 6.2.0 | Security filter chain and endpoint protection |

#### File Processing
| Technology | Version | Purpose |
|------------|---------|---------|
| Apache PDFBox | 3.0.0 | Server-side PDF processing and rendering |
| Apache Commons IO | 2.15.0 | File operations and streaming |
| PDF.js | Latest | Client-side PDF rendering in browser |

#### Frontend Technologies
- **HTML5** - Semantic markup and structure
- **CSS3** - Modern styling with flexbox and grid layouts
- **JavaScript (ES6+)** - Client-side logic and AJAX requests
- **Fetch API** - Asynchronous HTTP communication

#### Build & Development Tools
- **Maven 3.9+** - Dependency management and build automation
- **Spring Boot DevTools** - Hot reload and development utilities
- **Git** - Version control system

---

## Functional Requirements

### 1. User Management (FR-UM)
**FR-UM-01:** User Registration
- System shall allow new users to create accounts with unique usernames
- Passwords shall be hashed using BCrypt before storage
- Username validation shall prevent duplicates

**FR-UM-02:** User Authentication
- System shall authenticate users via username and password
- System shall issue JWT tokens with 24-hour expiration
- System shall validate tokens on protected endpoints

**FR-UM-03:** User Session Management
- System shall maintain stateless sessions using JWT
- System shall allow users to logout by clearing client-side tokens
- System shall provide account deletion functionality with cascade operations

### 2. Book Management (FR-BM)
**FR-BM-01:** Book Upload
- System shall accept PDF and EPUB files up to 50MB
- System shall store files in designated upload directory
- System shall capture metadata (title, authors, genres, publisher, language)
- System shall support multiple authors per book via many-to-many relationship

**FR-BM-02:** Book CRUD Operations
- System shall allow book owners to update book metadata
- System shall allow book owners to delete their uploaded books
- System shall prevent unauthorized users from modifying others' books
- System shall maintain referential integrity via foreign key constraints

**FR-BM-03:** Book Discovery
- System shall provide full-text search across title, author, and genre
- System shall display all books in community library view
- System shall show book details including authors, genres, publisher, language
- System shall calculate and display average ratings from user reviews

### 3. Library Management (FR-LM)
**FR-LM-01:** Personal Library
- System shall allow users to add community books to personal library
- System shall track reading progress as percentage (0-100%)
- System shall record date added and last read date
- System shall allow users to rate books (1-5 stars)
- System shall prevent duplicate entries (UNIQUE constraint on UserId + BookId)

**FR-LM-02:** Reading Status Tracking
- System shall categorize books as: Not Started (0%), Reading (1-99%), Completed (100%)
- System shall allow users to update progress manually
- System shall filter library by reading status

**FR-LM-03:** Library Analytics
- System shall calculate total books in user library
- System shall count currently reading books
- System shall count completed books
- System shall identify "hot books" based on library additions

### 4. Collection Management (FR-CM)
**FR-CM-01:** Collection CRUD
- System shall allow users to create named collections
- System shall timestamp collection creation
- System shall allow users to rename and delete collections
- System shall delete collections when owning user is deleted (CASCADE)

**FR-CM-02:** Collection-Book Associations
- System shall support many-to-many relationship between collections and books
- System shall allow adding/removing books from collections
- System shall display book count per collection
- System shall show all books within a collection

### 5. Reading Features (FR-RF)
**FR-RF-01:** In-Browser PDF Reader
- System shall stream PDF files for in-browser reading
- System shall provide page navigation controls
- System shall support zoom functionality
- System shall use PDF.js for client-side rendering

**FR-RF-02:** Bookmarks
- System shall allow users to bookmark specific pages
- System shall support optional notes on bookmarks
- System shall link bookmarks to USERBOOK entries
- System shall order bookmarks by page number

**FR-RF-03:** Annotations (Future Enhancement)
- System shall support text highlighting
- System shall allow annotation notes
- System shall persist annotations per user

### 6. Search & Filter (FR-SF)
**FR-SF-01:** Search Functionality
- System shall search across book names, authors, and genres
- System shall implement case-insensitive partial matching
- System shall return results ranked by relevance

**FR-SF-02:** Filter Capabilities
- System shall filter books by language
- System shall filter books by format (PDF, EPUB, etc.)
- System shall filter books by genre
- System shall filter library by reading status

---

## Non-Functional Requirements

### NFR-1: Performance
- API response time shall not exceed 500ms for book list queries
- File upload shall support resumable uploads for large files
- System shall handle concurrent users (target: 100+ simultaneous users)

### NFR-2: Security
- All passwords shall be hashed using BCrypt with minimum cost factor of 10
- JWT tokens shall use HS512 algorithm with strong secret key
- SQL injection shall be prevented via parameterized queries (PreparedStatement)
- File uploads shall be validated for type and size
- Unauthorized access attempts shall be logged

### NFR-3: Usability
- User interface shall be responsive across desktop and mobile devices
- System shall provide clear error messages for all failure cases
- Navigation shall be intuitive with maximum 3 clicks to any feature

### NFR-4: Reliability
- System shall use database transactions to ensure data consistency
- File uploads shall validate integrity before database commit
- System shall handle database connection failures gracefully

### NFR-5: Maintainability
- Code shall follow MVC architectural pattern
- Database shall be normalized to 3NF to minimize redundancy
- API endpoints shall follow RESTful conventions
- Code shall include comprehensive inline documentation

---

## Database Design

### Entity-Relationship Diagram (Conceptual)

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│    USER     │         │    BOOK     │         │   AUTHOR    │
├─────────────┤         ├─────────────┤         ├─────────────┤
│ UserId (PK) │         │ BookId (PK) │         │AuthorId(PK) │
│ UserName    │◄───────┐│ Name        │┌───────►│ AuthorName  │
│ HashedPwd   │        ││ Language    ││        └─────────────┘
└─────────────┘        ││ FilePath    ││
       │               ││ Format      ││        ┌─────────────┐
       │               ││ UserId (FK) ││        │   GENRE     │
       │               ││ PubId (FK)  ││        ├─────────────┤
       │               │└─────────────┘│        │ GenreId(PK) │
       │               │       │        └───────►│ GenreName   │
       │               │       │                 └─────────────┘
       │               │       │
       ├───────────────┼───────┤                ┌─────────────┐
       │               │       │                │  PUBLISHER  │
       ▼               ▼       ▼                ├─────────────┤
┌─────────────┐  ┌──────────────┐              │PubId (PK)   │
│  USERBOOK   │  │ COLLECTION   │◄─────────────┤ PubName     │
├─────────────┤  ├──────────────┤              └─────────────┘
│UBId (PK)    │  │ CollId (PK)  │
│ Progress    │  │ CollName     │
│ AddedDate   │  │ CreateDate   │
│ LastRead    │  │ UserId (FK)  │
│ UserRating  │  └──────────────┘
│ UserId (FK) │         │
│ BookId (FK) │         │
└─────────────┘         │
       │                │
       ▼                ▼
┌──────────────┐  ┌───────────────┐
│   BOOKMARK   │  │COLLECTIONBOOK │
├──────────────┤  ├───────────────┤
│ BMId (PK)    │  │CollId (FK,PK) │
│ PageNum      │  │BookId (FK,PK) │
│ Notes        │  └───────────────┘
│ UBId (FK)    │
└──────────────┘
```

### Database Schema (Normalized to 3NF)

#### Table: USER
```sql
CREATE TABLE USER (
    UserId INT AUTO_INCREMENT PRIMARY KEY,
    UserName VARCHAR(100) NOT NULL UNIQUE,
    HashedPassword VARCHAR(255) NOT NULL
);
```
**Purpose:** Stores user account information with secure password hashing.

#### Table: BOOK
```sql
CREATE TABLE BOOK (
    BookId INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Language VARCHAR(50),
    FilePath VARCHAR(1024) NOT NULL,
    Format VARCHAR(20),
    UserId INT,
    PublisherId INT,
    FOREIGN KEY (UserId) REFERENCES USER(UserId) ON DELETE SET NULL,
    FOREIGN KEY (PublisherId) REFERENCES PUBLISHER(PublisherId) ON DELETE SET NULL
);
```
**Purpose:** Central book repository. UserId indicates the uploader. ON DELETE SET NULL preserves books when users/publishers are deleted.

#### Table: USERBOOK
```sql
CREATE TABLE USERBOOK (
    UserBookId INT AUTO_INCREMENT PRIMARY KEY,
    Progress VARCHAR(50),
    AddedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    LastReadDate DATETIME,
    UserRating TINYINT,
    UserId INT,
    BookId INT,
    FOREIGN KEY (UserId) REFERENCES USER(UserId) ON DELETE CASCADE,
    FOREIGN KEY (BookId) REFERENCES BOOK(BookId) ON DELETE CASCADE,
    UNIQUE(UserId, BookId)
);
```
**Purpose:** Junction table representing user's personal library. Tracks reading progress and ratings. UNIQUE constraint prevents duplicate additions.

#### Table: COLLECTION
```sql
CREATE TABLE COLLECTION (
    CollectionId INT AUTO_INCREMENT PRIMARY KEY,
    CollectionName VARCHAR(255) NOT NULL,
    CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    UserId INT,
    FOREIGN KEY (UserId) REFERENCES USER(UserId) ON DELETE CASCADE
);
```
**Purpose:** User-created book collections (e.g., "Favorites", "To Read").

#### Table: COLLECTIONBOOK
```sql
CREATE TABLE COLLECTIONBOOK (
    CollectionId INT,
    BookId INT,
    PRIMARY KEY (CollectionId, BookId),
    FOREIGN KEY (CollectionId) REFERENCES COLLECTION(CollectionId) ON DELETE CASCADE,
    FOREIGN KEY (BookId) REFERENCES BOOK(BookId) ON DELETE CASCADE
);
```
**Purpose:** Many-to-many relationship between collections and books.

#### Table: AUTHOR, GENRE, PUBLISHER
```sql
CREATE TABLE AUTHOR (
    AuthorId INT AUTO_INCREMENT PRIMARY KEY,
    AuthorName VARCHAR(255) NOT NULL
);

CREATE TABLE GENRE (
    GenreId INT AUTO_INCREMENT PRIMARY KEY,
    GenreName VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE PUBLISHER (
    PublisherId INT AUTO_INCREMENT PRIMARY KEY,
    PublisherName VARCHAR(255) NOT NULL
);
```
**Purpose:** Master data tables for book metadata.

#### Table: AUTHORBOOK, GENREBOOK
```sql
CREATE TABLE AUTHORBOOK (
    AuthorId INT,
    BookId INT,
    PRIMARY KEY (AuthorId, BookId),
    FOREIGN KEY (AuthorId) REFERENCES AUTHOR(AuthorId) ON DELETE CASCADE,
    FOREIGN KEY (BookId) REFERENCES BOOK(BookId) ON DELETE CASCADE
);

CREATE TABLE GENREBOOK (
    GenreId INT,
    BookId INT,
    PRIMARY KEY (GenreId, BookId),
    FOREIGN KEY (GenreId) REFERENCES GENRE(GenreId) ON DELETE CASCADE,
    FOREIGN KEY (BookId) REFERENCES BOOK(BookId) ON DELETE CASCADE
);
```
**Purpose:** Many-to-many relationships (books can have multiple authors/genres).

#### Table: BOOKMARK
```sql
CREATE TABLE BOOKMARK (
    BookmarkId INT AUTO_INCREMENT PRIMARY KEY,
    PageNumber INT NOT NULL,
    Notes TEXT,
    UserBookId INT,
    FOREIGN KEY (UserBookId) REFERENCES USERBOOK(UserBookId) ON DELETE CASCADE
);
```
**Purpose:** Stores page bookmarks with optional notes. Links to USERBOOK to associate with specific user-book combinations.

### Database Normalization Analysis

**1NF (First Normal Form):** ✅
- All attributes contain atomic values
- No repeating groups or arrays
- Each table has a primary key

**2NF (Second Normal Form):** ✅
- Satisfies 1NF
- No partial dependencies (all non-key attributes depend on entire primary key)
- Junction tables (AUTHORBOOK, GENREBOOK, COLLECTIONBOOK) have composite keys where all attributes depend on the full key

**3NF (Third Normal Form):** ✅
- Satisfies 2NF
- No transitive dependencies
- Example: AuthorName depends only on AuthorId, not transitively through BookId

**BCNF (Boyce-Codd Normal Form):** ✅
- All determinants are candidate keys
- No anomalies in functional dependencies

### Referential Integrity & Cascade Rules

| Parent Table | Child Table | Relationship | On Delete |
|--------------|-------------|--------------|-----------|
| USER | BOOK | 1:N | SET NULL (preserve books) |
| USER | COLLECTION | 1:N | CASCADE (delete user's collections) |
| USER | USERBOOK | 1:N | CASCADE (delete user's library) |
| BOOK | USERBOOK | 1:N | CASCADE (remove from all libraries) |
| BOOK | COLLECTIONBOOK | 1:N | CASCADE (remove from collections) |
| COLLECTION | COLLECTIONBOOK | 1:N | CASCADE (delete collection contents) |
| USERBOOK | BOOKMARK | 1:N | CASCADE (delete associated bookmarks) |
| AUTHOR | AUTHORBOOK | 1:N | CASCADE (remove authorship links) |
| GENRE | GENREBOOK | 1:N | CASCADE (remove genre classifications) |

---

## API Design & Implementation

### RESTful API Architecture

The system implements a RESTful API following standard HTTP conventions:
- **GET:** Retrieve resources
- **POST:** Create new resources
- **PUT:** Update existing resources (full update)
- **DELETE:** Remove resources

All API endpoints return JSON responses with appropriate HTTP status codes:
- **200 OK:** Successful GET/PUT
- **201 Created:** Successful POST
- **204 No Content:** Successful DELETE
- **400 Bad Request:** Invalid input
- **401 Unauthorized:** Missing/invalid JWT
- **403 Forbidden:** Insufficient permissions
- **404 Not Found:** Resource doesn't exist
- **500 Internal Server Error:** Server-side failure

### API Endpoints Documentation

#### Authentication APIs (`/api/auth`)

**POST /api/auth/register**
```json
Request Body:
{
  "username": "string",
  "password": "string"
}

Response (201 Created):
{
  "message": "User registered successfully",
  "userId": 1
}
```
**Purpose:** Create new user account with hashed password.

**POST /api/auth/login**
```json
Request Body:
{
  "username": "string",
  "password": "string"
}

Response (200 OK):
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "userId": 1,
  "username": "string"
}
```
**Purpose:** Authenticate user and return JWT token.

#### Book Management APIs (`/api/books`)

**GET /api/books**
```
Headers: Authorization: Bearer {token}

Response (200 OK):
[
  {
    "bookId": 1,
    "name": "Clean Code",
    "language": "English",
    "format": "PDF",
    "userId": 1,
    "publisher": "Prentice Hall"
  }
]
```
**Purpose:** Retrieve all books in community library.

**GET /api/books/{id}/details**
```
Headers: Authorization: Bearer {token}

Response (200 OK):
{
  "bookId": 1,
  "name": "Clean Code",
  "authors": "Robert C. Martin, Michael Feathers",
  "genres": "Technology, Programming",
  "publisher": "Prentice Hall",
  "language": "English",
  "format": "PDF",
  "fileSize": "5.2 MB"
}
```
**Purpose:** Retrieve detailed book information with joined author/genre data.

**POST /api/books**
```
Headers: 
  Authorization: Bearer {token}
  Content-Type: multipart/form-data

Form Data:
  title: string
  language: string
  format: string
  authorIds: [1, 2]
  genreIds: [3, 4]
  publisherId: 5
  ebookContent: File

Response (201 Created):
{
  "bookId": 10,
  "message": "Book uploaded successfully"
}
```
**Purpose:** Upload new book with metadata and file.

**PUT /api/books/{id}**
```json
Headers: Authorization: Bearer {token}

Request Body:
{
  "name": "Clean Code - Updated",
  "language": "English",
  "format": "PDF"
}

Response (200 OK):
{
  "message": "Book updated successfully"
}
```
**Purpose:** Update book metadata (owner only).

**DELETE /api/books/{id}**
```
Headers: Authorization: Bearer {token}

Response (204 No Content)
```
**Purpose:** Delete book and associated file (owner only).

**GET /api/books/search?q={query}**
```
Headers: Authorization: Bearer {token}
Parameters: q (query string)

Response (200 OK): [array of matching books]
```
**Purpose:** Full-text search across title, authors, genres.

**GET /api/books/{id}/rating**
```
Headers: Authorization: Bearer {token}

Response (200 OK):
{
  "AverageRating": 4.5,
  "TotalRatings": 12
}
```
**Purpose:** Calculate average rating from user reviews.

#### User Library APIs (`/api/library`)

**GET /api/library**
```
Headers: Authorization: Bearer {token}

Response (200 OK):
[
  {
    "bookId": 1,
    "name": "Clean Code",
    "progress": "45%",
    "userRating": 5,
    "addedDate": "2025-11-01T10:30:00",
    "lastReadDate": "2025-11-08T14:20:00"
  }
]
```
**Purpose:** Retrieve user's personal library.

**POST /api/library/{bookId}**
```
Headers: Authorization: Bearer {token}

Response (201 Created):
{
  "message": "Book added to library"
}
```
**Purpose:** Add book from community to personal library.

**PUT /api/library/{bookId}/progress**
```json
Headers: Authorization: Bearer {token}

Request Body:
{
  "progress": "75%"
}

Response (200 OK):
{
  "message": "Progress updated"
}
```
**Purpose:** Update reading progress.

**PUT /api/library/{bookId}/rating**
```json
Headers: Authorization: Bearer {token}

Request Body:
{
  "rating": 4
}

Response (200 OK):
{
  "message": "Rating updated"
}
```
**Purpose:** Update book rating (1-5 stars).

**DELETE /api/library/{bookId}**
```
Headers: Authorization: Bearer {token}

Response (204 No Content)
```
**Purpose:** Remove book from personal library.

**GET /api/library/hot-books**
```
Headers: Authorization: Bearer {token}

Response (200 OK):
[
  {
    "bookId": 3,
    "name": "The Pragmatic Programmer",
    "addCount": 45
  }
]
```
**Purpose:** Retrieve most popular books by library additions.

#### Collection Management APIs (`/api/collections`)

**GET /api/collections**
```
Headers: Authorization: Bearer {token}

Response (200 OK):
[
  {
    "collectionId": 1,
    "collectionName": "Favorites",
    "createdDate": "2025-10-15T09:00:00",
    "bookCount": 8
  }
]
```
**Purpose:** Retrieve user's collections with book counts.

**POST /api/collections**
```json
Headers: Authorization: Bearer {token}

Request Body:
{
  "collectionName": "To Read"
}

Response (201 Created):
{
  "collectionId": 5,
  "message": "Collection created"
}
```
**Purpose:** Create new collection.

**GET /api/collections/{id}/books**
```
Headers: Authorization: Bearer {token}

Response (200 OK): [array of books in collection]
```
**Purpose:** Retrieve all books in a collection.

**POST /api/collections/{id}/books/{bookId}**
```
Headers: Authorization: Bearer {token}

Response (201 Created)
```
**Purpose:** Add book to collection.

**DELETE /api/collections/{id}/books/{bookId}**
```
Headers: Authorization: Bearer {token}

Response (204 No Content)
```
**Purpose:** Remove book from collection.

**DELETE /api/collections/{id}**
```
Headers: Authorization: Bearer {token}

Response (204 No Content)
```
**Purpose:** Delete collection (does not delete books).

#### Bookmark APIs (`/api/bookmarks`)

**GET /api/bookmarks/book/{bookId}**
```
Headers: Authorization: Bearer {token}

Response (200 OK):
[
  {
    "bookmarkId": 1,
    "pageNumber": 42,
    "notes": "Important concept about dependency injection",
    "userBookId": 5
  }
]
```
**Purpose:** Retrieve all bookmarks for a book in user's library.

**POST /api/bookmarks**
```json
Headers: Authorization: Bearer {token}

Request Body:
{
  "userBookId": 5,
  "pageNumber": 42,
  "notes": "Remember to review this section"
}

Response (201 Created):
{
  "bookmarkId": 8,
  "message": "Bookmark created"
}
```
**Purpose:** Create new bookmark.

**PUT /api/bookmarks/{id}**
```json
Headers: Authorization: Bearer {token}

Request Body:
{
  "notes": "Updated note"
}

Response (200 OK)
```
**Purpose:** Update bookmark notes.

**DELETE /api/bookmarks/{id}**
```
Headers: Authorization: Bearer {token}

Response (204 No Content)
```
**Purpose:** Delete bookmark.

#### Author/Genre/Publisher APIs

**GET /api/authors**
**GET /api/genres**
**GET /api/publishers**
```
Headers: Authorization: Bearer {token}

Response (200 OK): [array of entities]
```
**Purpose:** Retrieve master data for metadata selection.

**GET /api/authors/search?q={query}**
**GET /api/genres/search?q={query}**
**GET /api/publishers/search?q={query}**
```
Headers: Authorization: Bearer {token}

Response (200 OK): [array of matching entities]
```
**Purpose:** Autocomplete support for book upload.

#### User Management APIs (`/api/users`)

**DELETE /api/users/me**
```
Headers: Authorization: Bearer {token}

Response (200 OK):
{
  "message": "Account deleted successfully"
}
```
**Purpose:** Delete user account with cascade deletion of library, collections, bookmarks. Books uploaded by user are preserved (UserId set to NULL).

### Security Implementation

#### JWT Token Structure
```json
{
  "header": {
    "alg": "HS512",
    "typ": "JWT"
  },
  "payload": {
    "sub": "username",
    "userId": 1,
    "iat": 1699200000,
    "exp": 1699286400
  },
  "signature": "HMACSHA512(base64Url(header) + '.' + base64Url(payload), secret)"
}
```

#### Authentication Flow
1. User submits credentials to `/api/auth/login`
2. `AuthService` validates username/password using BCrypt
3. `JwtTokenProvider` generates signed JWT token
4. Client stores token in `localStorage`
5. Client includes token in `Authorization: Bearer {token}` header
6. `JwtAuthenticationFilter` intercepts requests
7. Filter validates token signature and expiration
8. Filter extracts userId and injects into `SecurityContext`
9. Controller accesses `UserPrincipal` from `SecurityContext`

#### Password Hashing
```java
// Registration
String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));

// Authentication
boolean matches = BCrypt.checkpw(plainPassword, storedHashedPassword);
```

---

## Project Structure & Architecture

### Layered Architecture Pattern

The project follows a traditional 3-tier architecture:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  (HTML/CSS/JS - User Interface)         │
└─────────────────┬───────────────────────┘
                  │ HTTP/REST
┌─────────────────▼───────────────────────┐
│         Application Layer               │
│    (Controllers, Services, Security)    │
│  - AuthController, BookController       │
│  - AuthService, FileStorageService      │
│  - JwtAuthenticationFilter              │
└─────────────────┬───────────────────────┘
                  │ JDBC
┌─────────────────▼───────────────────────┐
│         Data Access Layer               │
│    (DAOs, ConnectionManager)            │
│  - UserDAO, BookDAO, UserBookDAO        │
│  - PreparedStatement, ResultSet         │
└─────────────────┬───────────────────────┘
                  │ MySQL Protocol
┌─────────────────▼───────────────────────┐
│         Data Storage Layer              │
│        (MySQL Database)                 │
│  - 11 Normalized Tables                 │
│  - Foreign Key Constraints              │
└─────────────────────────────────────────┘
```

### Directory Structure

```
book-library-webapp/
├── src/
│   ├── main/
│   │   ├── java/com/dbinteract/
│   │   │   ├── BookLibraryApplication.java      # Spring Boot entry point
│   │   │   │
│   │   │   ├── config/                          # Configuration classes
│   │   │   │   ├── FileStorageConfig.java       # File upload configuration
│   │   │   │   └── SecurityConfig.java          # Spring Security config
│   │   │   │
│   │   │   ├── controller/                      # REST Controllers (API endpoints)
│   │   │   │   ├── AuthController.java          # /api/auth/* endpoints
│   │   │   │   ├── BookController.java          # /api/books/* endpoints
│   │   │   │   ├── UserBookController.java      # /api/library/* endpoints
│   │   │   │   ├── CollectionController.java    # /api/collections/* endpoints
│   │   │   │   ├── BookmarkController.java      # /api/bookmarks/* endpoints
│   │   │   │   ├── AuthorController.java        # /api/authors/* endpoints
│   │   │   │   ├── GenreController.java         # /api/genres/* endpoints
│   │   │   │   ├── PublisherController.java     # /api/publishers/* endpoints
│   │   │   │   ├── UserController.java          # /api/users/* endpoints
│   │   │   │   └── GlobalExceptionHandler.java  # Centralized error handling
│   │   │   │
│   │   │   ├── dao/                             # Data Access Objects
│   │   │   │   ├── UserDAO.java                 # USER table operations
│   │   │   │   ├── BookDAO.java                 # BOOK table operations
│   │   │   │   ├── UserBookDAO.java             # USERBOOK table operations
│   │   │   │   ├── CollectionDAO.java           # COLLECTION table operations
│   │   │   │   ├── BookmarkDAO.java             # BOOKMARK table operations
│   │   │   │   ├── AuthorDAO.java               # AUTHOR table operations
│   │   │   │   ├── GenreDAO.java                # GENRE table operations
│   │   │   │   └── PublisherDAO.java            # PUBLISHER table operations
│   │   │   │
│   │   │   ├── database/
│   │   │   │   └── ConnectionManager.java       # Singleton DB connection pool
│   │   │   │
│   │   │   ├── dto/                             # Data Transfer Objects
│   │   │   │   ├── LoginRequest.java            # Login API request
│   │   │   │   ├── RegisterRequest.java         # Registration API request
│   │   │   │   └── AuthResponse.java            # Auth API response with JWT
│   │   │   │
│   │   │   ├── models/                          # Domain entities
│   │   │   │   ├── User.java                    # USER entity
│   │   │   │   ├── Book.java                    # BOOK entity
│   │   │   │   ├── UserBook.java                # USERBOOK entity
│   │   │   │   ├── Collection.java              # COLLECTION entity
│   │   │   │   ├── Bookmark.java                # BOOKMARK entity
│   │   │   │   ├── Author.java                  # AUTHOR entity
│   │   │   │   ├── Genre.java                   # GENRE entity
│   │   │   │   └── Publisher.java               # PUBLISHER entity
│   │   │   │
│   │   │   ├── security/                        # Security components
│   │   │   │   ├── JwtTokenProvider.java        # JWT generation/validation
│   │   │   │   ├── JwtAuthenticationFilter.java # Request interceptor
│   │   │   │   ├── UserDetailsServiceImpl.java  # Spring Security integration
│   │   │   │   └── UserPrincipal.java           # Authenticated user wrapper
│   │   │   │
│   │   │   ├── service/                         # Business logic layer
│   │   │   │   ├── AuthService.java             # Authentication business logic
│   │   │   │   └── FileStorageService.java      # File I/O operations
│   │   │   │
│   │   │   └── utils/
│   │   │       └── PasswordHasher.java          # BCrypt utility methods
│   │   │
│   │   └── resources/
│   │       ├── application.properties           # Application configuration
│   │       ├── database.sql                     # DB schema + sample data
│   │       │
│   │       └── static/                          # Frontend assets (served by Spring)
│   │           ├── index.html                   # Landing page
│   │           ├── login.html                   # Login form
│   │           ├── register.html                # Registration form
│   │           ├── dashboard.html               # User dashboard
│   │           ├── library.html                 # Personal library view
│   │           ├── community.html               # Community books browse
│   │           ├── upload.html                  # Book upload form
│   │           ├── reader.html                  # PDF reader interface
│   │           │
│   │           ├── css/
│   │           │   ├── style.css                # Main stylesheet
│   │           │   └── reader.css               # Reader-specific styles
│   │           │
│   │           └── js/
│   │               ├── auth.js                  # Authentication logic
│   │               ├── app.js                   # Main application logic
│   │               ├── upload.js                # Upload functionality
│   │               └── reader.js                # PDF rendering logic
│   │
│   └── test/java/                               # Unit and integration tests
│
├── target/                                      # Maven build output
│   └── book-library-webapp-2.0.0.jar           # Executable JAR
│
├── uploads/                                     # Uploaded book files
│
├── pom.xml                                      # Maven project descriptor
├── .gitignore                                   # Git ignore rules
└── README.md                                    # This documentation
```

### Design Patterns Implemented

#### 1. Data Access Object (DAO) Pattern
**Purpose:** Separate persistence logic from business logic.
```java
public interface BookDAO {
    Book findById(int bookId) throws SQLException;
    List<Book> findAll() throws SQLException;
    int insert(Book book) throws SQLException;
    void update(Book book) throws SQLException;
    void delete(int bookId) throws SQLException;
}
```

#### 2. Singleton Pattern
**Purpose:** Ensure single database connection pool instance.
```java
public class ConnectionManager {
    private static ConnectionManager instance;
    private HikariDataSource dataSource;
    
    private ConnectionManager() {
        // Initialize connection pool
    }
    
    public static synchronized ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }
}
```

#### 3. Model-View-Controller (MVC) Pattern
- **Model:** Entity classes (User, Book, etc.)
- **View:** HTML/CSS/JavaScript frontend
- **Controller:** Spring REST Controllers

#### 4. Dependency Injection Pattern
**Purpose:** Loose coupling via Spring's IoC container.
```java
@RestController
public class BookController {
    private final BookDAO bookDAO;
    
    @Autowired
    public BookController(BookDAO bookDAO) {
        this.bookDAO = bookDAO;  // Injected by Spring
    }
}
```

#### 5. Filter Chain Pattern
**Purpose:** Request processing pipeline.
```
HTTP Request → SecurityFilterChain → JwtAuthenticationFilter → Controller
```

---

## Installation & Setup

### Prerequisites

| Software | Minimum Version | Purpose |
|----------|-----------------|---------|
| Java JDK | 21 LTS | Runtime environment |
| MySQL | 8.0 | Database server |
| Maven | 3.9+ | Build tool |
| Git | 2.0+ | Version control |

#### Verify Installations
```bash
java --version       # Should show Java 21+
mvn --version        # Should show Maven 3.9+
mysql --version      # Should show MySQL 8.0+
```

### Step-by-Step Setup Guide

#### 1. Clone Repository
```bash
git clone https://github.com/Huyitit/mySQLGUI.git
cd mySQLGUI
```

#### 2. Database Setup
```bash
# Start MySQL server
# Windows: Open MySQL Workbench or use services.msc
# Linux: sudo systemctl start mysql
# macOS: brew services start mysql

# Login to MySQL
mysql -u root -p

# Execute schema script
source src/main/resources/database.sql

# OR use command line
mysql -u root -p < src/main/resources/database.sql
```

**Verify Database Creation:**
```sql
USE book_library;
SHOW TABLES;  -- Should show 11 tables
SELECT COUNT(*) FROM USER;  -- Should show 3 sample users
```

#### 3. Configure Application
Edit `src/main/resources/application.properties`:
```properties
# Update with your MySQL credentials
spring.datasource.url=jdbc:mysql://localhost:3306/book_library?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

# Generate strong JWT secret for production
jwt.secret=CHANGE_THIS_TO_STRONG_RANDOM_STRING_IN_PRODUCTION
jwt.expiration=86400000

# Specify upload directory (optional, defaults to ./uploads)
file.upload-dir=./uploads
```

**Generate Strong JWT Secret:**
```bash
# Option 1: Using OpenSSL
openssl rand -base64 64

# Option 2: Using Python
python -c "import secrets; print(secrets.token_urlsafe(64))"

# Option 3: Using PowerShell
[Convert]::ToBase64String((1..64|%{Get-Random -Max 256}))
```

#### 4. Build Project
```bash
# Clean previous builds and package
mvn clean package

# Skip tests for faster build (optional)
mvn clean package -DskipTests
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: 15.234 s
[INFO] Finished at: 2025-11-09T10:30:00+07:00
```

The compiled JAR will be created at:
```
target/book-library-webapp-2.0.0.jar
```

#### 5. Run Application
```bash
# Option 1: Run with Maven
mvn spring-boot:run

# Option 2: Run JAR directly
java -jar target/book-library-webapp-2.0.0.jar

# Option 3: Run with custom port
java -jar target/book-library-webapp-2.0.0.jar --server.port=9090
```

**Successful Startup Indicators:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

2025-11-09 10:30:15 - Started BookLibraryApplication in 5.234 seconds
2025-11-09 10:30:15 - Tomcat started on port(s): 8080 (http)
```

#### 6. Access Application
Open web browser and navigate to:
```
http://localhost:8080
```

**Test Login:**
- Username: `an_nguyen`
- Password: `password123`

### Deployment Configurations

#### Development Environment
```properties
# application-dev.properties
spring.profiles.active=dev
logging.level.com.dbinteract=DEBUG
spring.boot.devtools.restart.enabled=true
```

#### Production Environment
```properties
# application-prod.properties
spring.profiles.active=prod
logging.level.root=WARN
logging.level.com.dbinteract=INFO

# Use environment variables for sensitive data
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}
```

**Run with Profile:**
```bash
java -jar target/book-library-webapp-2.0.0.jar --spring.profiles.active=prod
```

---

## Usage Guide & Testing

### Sample User Accounts

| Username | Password | Library Stats | Collections | Purpose |
|----------|----------|---------------|-------------|---------|
| `an_nguyen` | `password123` | 5 books, 2 reading | 2 collections | General testing |
| `binh_le` | `password123` | 3 books, 1 completed | 1 collection | Rating testing |
| `chi_tran` | `password123` | 4 books, 5 bookmarks | 0 collections | Bookmark testing |

### Functional Testing Scenarios

#### Test Case 1: User Registration & Authentication
```
1. Navigate to http://localhost:8080/register.html
2. Enter unique username (e.g., "testuser123")
3. Enter password (e.g., "password456")
4. Click "Register"
5. Verify redirect to login page
6. Login with created credentials
7. Verify JWT token stored in localStorage
8. Verify redirect to dashboard

Expected: Successful registration, login, and dashboard access
```

#### Test Case 2: Book Upload
```
1. Login as an_nguyen
2. Navigate to /upload.html
3. Fill in book details:
   - Title: "Test Book"
   - Language: "English"
   - Format: "PDF"
4. Select sample PDF file (< 50MB)
5. Select authors from autocomplete
6. Select genres from autocomplete
7. Click "Upload"
8. Verify success message
9. Navigate to /community.html
10. Verify book appears in community library

Expected: Book uploaded with metadata correctly associated
```

#### Test Case 3: Add to Library & Track Progress
```
1. Login as binh_le
2. Navigate to /community.html
3. Find a book not in library
4. Click "Add to Library"
5. Navigate to /library.html
6. Verify book appears
7. Click "Info" button
8. Update progress to "50%"
9. Rate book 4 stars
10. Refresh page
11. Verify progress and rating persisted

Expected: Book added to library with progress/rating tracked
```

#### Test Case 4: Create Collection & Add Books
```
1. Login as chi_tran
2. Navigate to /dashboard.html
3. Click "Create Collection"
4. Enter name: "Favorites"
5. Navigate to /community.html
6. For each book, click "Add to Collection"
7. Select "Favorites" collection
8. Return to /dashboard.html
9. Click "View Books" on Favorites collection
10. Verify all added books appear

Expected: Collection created with books successfully added
```

#### Test Case 5: PDF Reader & Bookmarks
```
1. Login as an_nguyen
2. Navigate to /library.html
3. Click "Read" on any PDF book
4. Wait for PDF to load
5. Navigate to page 10
6. Click "Bookmark" button
7. Enter note: "Important section"
8. Navigate to page 25
9. Add another bookmark
10. Click "Bookmarks" panel
11. Verify both bookmarks listed
12. Click bookmark to navigate

Expected: PDF renders correctly, bookmarks save and navigate
```

#### Test Case 6: Search & Filter
```
1. Login as any user
2. Navigate to /community.html
3. Test search:
   - Enter "Clean" in search box
   - Verify "Clean Code" appears
4. Test language filter:
   - Select "English"
   - Verify only English books shown
5. Test format filter:
   - Select "PDF"
   - Verify only PDF books shown
6. Test combined filters
7. Clear filters
8. Verify all books restored

Expected: Search and filters work independently and combined
```

#### Test Case 7: Account Deletion (Cascade)
```
1. Create new test account
2. Upload a book
3. Add books to library
4. Create collections
5. Add bookmarks
6. Logout
7. Login again
8. Navigate to user dropdown
9. Click "Delete Account"
10. Confirm deletion
11. Verify redirect to login
12. Attempt to login with deleted credentials
13. Verify login fails
14. Login as different user
15. Navigate to community
16. Verify uploaded book still exists (UserId = NULL)

Expected: User deleted, library/collections/bookmarks removed, books preserved
```

### API Testing with cURL

#### Test Authentication
```bash
# Register new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"apitest","password":"test123"}'

# Login and capture token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"an_nguyen","password":"password123"}' \
  -o response.json

# Extract token (Linux/macOS)
TOKEN=$(jq -r '.token' response.json)

# Extract token (Windows PowerShell)
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" `
  -Method POST -ContentType "application/json" `
  -Body '{"username":"an_nguyen","password":"password123"}'
$TOKEN = $response.token
```

#### Test Book APIs
```bash
# Get all books
curl -X GET http://localhost:8080/api/books \
  -H "Authorization: Bearer $TOKEN"

# Get book details
curl -X GET http://localhost:8080/api/books/1/details \
  -H "Authorization: Bearer $TOKEN"

# Search books
curl -X GET "http://localhost:8080/api/books/search?q=Clean" \
  -H "Authorization: Bearer $TOKEN"

# Upload book
curl -X POST http://localhost:8080/api/books \
  -H "Authorization: Bearer $TOKEN" \
  -F "title=API Test Book" \
  -F "language=English" \
  -F "format=PDF" \
  -F "authorIds=[1,2]" \
  -F "genreIds=[3]" \
  -F "publisherId=1" \
  -F "ebookContent=@/path/to/book.pdf"
```

#### Test Library APIs
```bash
# Get user library
curl -X GET http://localhost:8080/api/library \
  -H "Authorization: Bearer $TOKEN"

# Add book to library
curl -X POST http://localhost:8080/api/library/5 \
  -H "Authorization: Bearer $TOKEN"

# Update progress
curl -X PUT http://localhost:8080/api/library/5/progress \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"progress":"75%"}'

# Update rating
curl -X PUT http://localhost:8080/api/library/5/rating \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"rating":5}'
```

### Performance Testing

#### Load Testing with Apache Bench
```bash
# Test GET endpoint (100 requests, 10 concurrent)
ab -n 100 -c 10 -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/books

# Test POST endpoint (file upload)
ab -n 10 -c 2 -H "Authorization: Bearer $TOKEN" \
  -p bookdata.json -T application/json \
  http://localhost:8080/api/library/1
```

**Expected Results:**
- Average response time: < 500ms
- Failed requests: 0
- Throughput: > 50 requests/sec

---

## Troubleshooting

### Common Issues & Solutions

#### Issue 1: "Access denied for user 'root'@'localhost'"
**Cause:** Incorrect MySQL credentials or privileges

**Solution:**
```sql
-- Login to MySQL as root
mysql -u root -p

-- Grant privileges
GRANT ALL PRIVILEGES ON book_library.* TO 'root'@'localhost';
FLUSH PRIVILEGES;

-- Or create dedicated user
CREATE USER 'booklib'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON book_library.* TO 'booklib'@'localhost';
```

Update `application.properties`:
```properties
spring.datasource.username=booklib
spring.datasource.password=password
```

#### Issue 2: "Unknown database 'book_library'"
**Cause:** Database not created

**Solution:**
```bash
mysql -u root -p < src/main/resources/database.sql
```

Or manually:
```sql
SOURCE src/main/resources/database.sql;
```

#### Issue 3: "Could not create upload directory"
**Cause:** Insufficient file system permissions

**Solution:**
```bash
# Create directory manually
mkdir uploads
chmod 755 uploads  # Linux/macOS

# Or change upload path in application.properties
file.upload-dir=C:/Users/YourName/Documents/uploads  # Windows
file.upload-dir=/home/yourname/uploads                # Linux
```

#### Issue 4: "Invalid JWT token"
**Cause:** Token expired or secret key changed

**Solution:**
- **Client-side:** Clear localStorage and login again
- **Server-side:** Ensure `jwt.secret` in `application.properties` hasn't changed
- **Check expiration:** Default is 24 hours (86400000 ms)

```javascript
// Browser console
localStorage.clear();
location.reload();
```

#### Issue 5: "Failed to bind to 0.0.0.0:8080"
**Cause:** Port already in use

**Solution:**
```bash
# Find process using port 8080
# Windows
netstat -ano | findstr :8080
taskkill /PID <process_id> /F

# Linux/macOS
lsof -i :8080
kill -9 <PID>

# Or change port
java -jar target/book-library-webapp-2.0.0.jar --server.port=9090
```

#### Issue 6: "File size exceeds maximum"
**Cause:** Upload file > 50MB

**Solution:**
Update `application.properties`:
```properties
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB
```

#### Issue 7: "Cannot delete or update a parent row"
**Cause:** Foreign key constraint violation

**Solution:**
- Already fixed in UserDAO with transaction-based deletion
- First sets BOOK.UserId to NULL
- Then deletes USER (CASCADE handles USERBOOK, COLLECTION, BOOKMARK)

#### Issue 8: "ClassNotFoundException: com.mysql.cj.jdbc.Driver"
**Cause:** MySQL connector not in classpath

**Solution:**
```bash
# Verify dependency in pom.xml
mvn dependency:tree | grep mysql

# Rebuild
mvn clean install
```

#### Issue 9: "CORS policy: No 'Access-Control-Allow-Origin' header"
**Cause:** Accessing API from different origin (frontend served separately)

**Solution:**
Add CORS configuration to `SecurityConfig.java`:
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedOrigin("http://localhost:3000");
    configuration.addAllowedMethod("*");
    configuration.addAllowedHeader("*");
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

### Debugging Tips

#### Enable Verbose Logging
```properties
# application.properties
logging.level.com.dbinteract=DEBUG
logging.level.org.springframework.security=TRACE
logging.level.org.springframework.jdbc=DEBUG
```

#### Check Database Connections
```sql
-- MySQL
SHOW PROCESSLIST;
SHOW STATUS LIKE 'Threads_connected';
```

#### Monitor File Uploads
```bash
# Watch upload directory
# Linux/macOS
watch -n 1 ls -lh uploads/

# Windows PowerShell
while ($true) { Get-ChildItem uploads; Start-Sleep -Seconds 1; Clear-Host }
```

#### Test Database Connectivity
```bash
# Test connection from Java
java -cp target/book-library-webapp-2.0.0.jar:mysql-connector-j-*.jar \
  com.dbinteract.database.ConnectionManager
```

---

## Project Evaluation Criteria

### Technical Implementation (40%)
- ✅ **Database Design (15%)**
  - Normalized to 3NF
  - Proper foreign key constraints
  - Efficient indexing on frequently queried columns
  - CASCADE rules preserve data integrity

- ✅ **Backend Development (15%)**
  - RESTful API design following conventions
  - Secure authentication with JWT and BCrypt
  - Proper exception handling and error responses
  - Transaction management for data consistency

- ✅ **Frontend Development (10%)**
  - Responsive UI design
  - Asynchronous communication with Fetch API
  - Client-side PDF rendering
  - Proper state management

### Code Quality (25%)
- ✅ **Architecture (10%)**
  - Clear separation of concerns (MVC pattern)
  - DAO pattern for database abstraction
  - Dependency injection
  - Singleton pattern for connection pool

- ✅ **Code Organization (10%)**
  - Logical package structure
  - Meaningful class and method names
  - Consistent coding style
  - No code duplication

- ✅ **Documentation (5%)**
  - Comprehensive README
  - Inline code comments
  - API documentation
  - Database schema documentation

### Functionality (25%)
- ✅ **Core Features (15%)**
  - User registration and authentication
  - Book upload with metadata
  - Personal library management
  - Collection organization
  - PDF reading functionality

- ✅ **Advanced Features (10%)**
  - Search and filtering
  - Reading progress tracking
  - Rating system
  - Bookmark with notes
  - Many-to-many relationships

### Security (10%)
- ✅ **Authentication & Authorization (5%)**
  - JWT token-based auth
  - Password hashing with BCrypt
  - Protected endpoints

- ✅ **Data Security (5%)**
  - SQL injection prevention (PreparedStatement)
  - File upload validation
  - Ownership verification for CRUD operations

---

## Future Enhancements

### Phase 1: Enhanced Reading Experience
- [ ] **EPUB Reader** - Native browser-based EPUB rendering
- [ ] **Annotation Tools** - Highlight text, underline, draw
- [ ] **Text-to-Speech** - Audio playback of book content
- [ ] **Night Mode** - Dark theme for reader
- [ ] **Font Customization** - Size, family, line spacing

### Phase 2: Social Features
- [ ] **User Profiles** - Public profile pages
- [ ] **Book Reviews** - Detailed written reviews
- [ ] **Comments** - Discussion threads on books
- [ ] **Book Clubs** - Group reading sessions
- [ ] **Following System** - Follow users and see their activities

### Phase 3: Recommendations & Analytics
- [ ] **Recommendation Engine** - ML-based book suggestions
- [ ] **Reading Statistics** - Charts and graphs of reading habits
- [ ] **Goal Setting** - Reading challenges and goals
- [ ] **Export Data** - Download reading history as CSV/JSON

### Phase 4: Technical Improvements
- [ ] **Caching Layer** - Redis for frequently accessed data
- [ ] **Search Engine** - Elasticsearch for advanced search
- [ ] **Microservices** - Split into independent services
- [ ] **Message Queue** - Async processing with RabbitMQ
- [ ] **Cloud Storage** - AWS S3 for file storage
- [ ] **CDN Integration** - CloudFront for static assets
- [ ] **Unit Tests** - JUnit 5 with 80%+ coverage
- [ ] **Integration Tests** - Spring Boot Test
- [ ] **API Documentation** - Swagger/OpenAPI
- [ ] **CI/CD Pipeline** - GitHub Actions
- [ ] **Docker Compose** - Multi-container setup
- [ ] **Kubernetes** - Container orchestration

### Phase 5: Mobile Support
- [ ] **Progressive Web App (PWA)** - Installable web app
- [ ] **Offline Reading** - Service Workers for offline access
- [ ] **Mobile-Optimized UI** - Touch-friendly interface
- [ ] **Native Mobile Apps** - iOS and Android

---

## References & Resources

### Technologies Documentation
1. **Spring Boot Documentation** - https://docs.spring.io/spring-boot/docs/3.2.0/reference/html/
2. **Spring Security Reference** - https://docs.spring.io/spring-security/reference/
3. **JWT Specification (RFC 7519)** - https://datatracker.ietf.org/doc/html/rfc7519
4. **MySQL 8.0 Reference Manual** - https://dev.mysql.com/doc/refman/8.0/en/
5. **PDF.js Documentation** - https://mozilla.github.io/pdf.js/
6. **Apache PDFBox User Guide** - https://pdfbox.apache.org/2.0/getting-started.html

### Design Patterns
7. **Gang of Four Design Patterns** - Gamma, Helm, Johnson, Vlissides (1994)
8. **DAO Pattern** - Core J2EE Patterns
9. **MVC Pattern** - Trygve Reenskaug (1979)

### Database Design
10. **Database System Concepts** - Silberschatz, Korth, Sudarshan (7th Edition)
11. **SQL and Relational Theory** - C.J. Date (3rd Edition)
12. **Database Normalization** - E.F. Codd (1971)

### Web Security
13. **OWASP Top 10** - https://owasp.org/www-project-top-ten/
14. **JWT Best Practices** - https://tools.ietf.org/html/rfc8725
15. **BCrypt Algorithm** - Niels Provos, David Mazières (1999)

---

## License & Academic Integrity

### Project License
This project is developed for educational purposes as part of the Database Systems / Web Application Development course at PTIT.

**Academic Use:**
- Students may reference this project for learning purposes
- Direct copying without understanding is prohibited
- Proper citation required if used in academic work

**Citation Format:**
```
[Your Name]. (2025). Book Library Management System. 
Database Systems Project, PTIT. Retrieved from [repository URL]
```

### Contact Information
**Student:** [Your Name]  
**Student ID:** [Your ID]  
**Email:** [Your Email]  
**GitHub:** [Your GitHub Profile]  
**Institution:** Posts and Telecommunications Institute of Technology  
**Course:** Database Systems / Web Application Development  
**Semester:** 1, Academic Year 2025-2026  

---

## Acknowledgments

### Instructors & Mentors
- Professor [Instructor Name] - Database Systems Course
- Teaching Assistants - Technical guidance and support

### Open Source Contributors
- Spring Framework Team
- MySQL Development Team
- Mozilla PDF.js Project
- Apache PDFBox Contributors
- All open-source library maintainers

### Testing & Feedback
- Classmates who provided user feedback
- Beta testers who helped identify bugs
- Code reviewers who improved code quality

---

**Last Updated:** November 9, 2025  
**Version:** 2.0.0  
**Status:** ✅ Production Ready

## 🛠️ Technology Stack

### Backend
- **Java 17** - Modern Java with latest features
- **Spring Boot 3.2.0** - Enterprise-grade application framework
- **Spring Security** - Authentication and authorization
- **Spring Web** - RESTful API development
- **MySQL 8.0+** - Relational database
- **JDBC** - Direct database access via DAO pattern
- **JWT (JSON Web Tokens)** - Stateless authentication
- **Apache PDFBox 3.0** - PDF processing and rendering
- **BCrypt** - Secure password hashing

### Frontend
- **HTML5/CSS3** - Modern, responsive UI
- **Vanilla JavaScript** - No framework dependencies
- **PDF.js** - Client-side PDF rendering
- **Fetch API** - Asynchronous HTTP requests

### Build & DevOps
- **Maven** - Dependency management and build automation
- **Spring Boot DevTools** - Hot reload during development

### Build & DevOps
- **Maven** - Dependency management and build automation
- **Spring Boot DevTools** - Hot reload during development

## 📋 Prerequisites

- **Java 17** or higher
- **MySQL 8.0** or higher
- **Maven 3.6** or higher
- Modern web browser (Chrome, Firefox, Safari, Edge)

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/book-library-webapp.git
cd book-library-webapp
```

### 2. Database Setup

Start your MySQL server and create the database:

```bash
# Login to MySQL
mysql -u root -p

# Create database and tables
source src/main/resources/database.sql
```

Or using command line:

```bash
mysql -u root -p < src/main/resources/database.sql
```

### 3. Configure Application

Edit `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/book_library?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_mysql_password

# JWT Secret (IMPORTANT: Change this in production!)
jwt.secret=YourSecretKeyHere-ChangeThisInProduction!
jwt.expiration=86400000

# File Upload Directory
file.upload-dir=./uploads
```

### 4. Build the Application

```bash
# Clean and package
mvn clean package

# Skip tests (optional)
mvn clean package -DskipTests
```

### 5. Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Or run the JAR directly
java -jar target/book-library-webapp-2.0.0.jar
```

### 6. Access the Application

Open your browser and navigate to:

```
http://localhost:8080
```

## 👤 Default Users

The database includes sample users for testing:

| Username | Password | Description |
|----------|----------|-------------|
| `an_nguyen` | `password123` | Sample user with library |
| `binh_le` | `password123` | Sample user with collections |
| `chi_tran` | `password123` | Sample user with bookmarks |

| `an_nguyen` | `password123` | Sample user with library |
| `binh_le` | `password123` | Sample user with collections |
| `chi_tran` | `password123` | Sample user with bookmarks |

## 📁 Project Structure

```
book-library-webapp/
├── src/
│   ├── main/
│   │   ├── java/com/dbinteract/
│   │   │   ├── BookLibraryApplication.java    # Main application entry point
│   │   │   ├── config/                        # Configuration classes
│   │   │   │   ├── FileStorageConfig.java     # File upload configuration
│   │   │   │   └── SecurityConfig.java        # Spring Security setup
│   │   │   ├── controller/                    # REST API Controllers
│   │   │   │   ├── AuthController.java        # Login/Register endpoints
│   │   │   │   ├── BookController.java        # Book CRUD operations
│   │   │   │   ├── UserBookController.java    # User library management
│   │   │   │   ├── CollectionController.java  # Collection management
│   │   │   │   ├── BookmarkController.java    # Bookmarks & highlights
│   │   │   │   ├── AuthorController.java      # Author operations
│   │   │   │   ├── GenreController.java       # Genre operations
│   │   │   │   ├── PublisherController.java   # Publisher operations
│   │   │   │   └── GlobalExceptionHandler.java# Error handling
│   │   │   ├── dao/                           # Data Access Objects
│   │   │   │   ├── UserDAO.java
│   │   │   │   ├── BookDAO.java
│   │   │   │   ├── UserBookDAO.java
│   │   │   │   ├── CollectionDAO.java
│   │   │   │   ├── BookmarkDAO.java
│   │   │   │   ├── AuthorDAO.java
│   │   │   │   ├── GenreDAO.java
│   │   │   │   └── PublisherDAO.java
│   │   │   ├── database/
│   │   │   │   └── ConnectionManager.java     # Database connection pooling
│   │   │   ├── dto/                           # Data Transfer Objects
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   └── AuthResponse.java
│   │   │   ├── models/                        # Entity classes
│   │   │   │   ├── User.java
│   │   │   │   ├── Book.java
│   │   │   │   ├── UserBook.java
│   │   │   │   ├── Collection.java
│   │   │   │   ├── Bookmark.java
│   │   │   │   ├── Author.java
│   │   │   │   ├── Genre.java
│   │   │   │   └── Publisher.java
│   │   │   ├── security/                      # Security components
│   │   │   │   ├── JwtTokenProvider.java      # JWT token generation/validation
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── UserDetailsServiceImpl.java
│   │   │   │   └── UserPrincipal.java
│   │   │   ├── service/                       # Business logic
│   │   │   │   ├── AuthService.java           # Authentication service
│   │   │   │   └── FileStorageService.java    # File upload/download
│   │   │   └── utils/
│   │   │       └── PasswordHasher.java        # BCrypt password utilities
│   │   └── resources/
│   │       ├── application.properties         # App configuration
│   │       ├── database.sql                   # Database schema & sample data
│   │       └── static/                        # Frontend files
│   │           ├── index.html                 # Landing page
│   │           ├── login.html                 # Login page
│   │           ├── register.html              # Registration page
│   │           ├── dashboard.html             # Main dashboard
│   │           ├── library.html               # User's library
│   │           ├── community.html             # Community books
│   │           ├── upload.html                # Upload books
│   │           ├── my-uploads.html            # Manage uploads
│   │           ├── reader.html                # PDF reader
│   │           ├── css/
│   │           │   ├── style.css              # Main styles
│   │           │   └── reader.css             # Reader styles
│   │           └── js/
│   │               ├── auth.js                # Authentication logic
│   │               ├── app.js                 # Main app logic
│   │               ├── upload.js              # Upload functionality
│   │               ├── my-uploads.js          # Upload management
│   │               └── reader.js              # PDF reader logic
├── target/                                    # Build output (ignored in git)
├── uploads/                                   # Uploaded files (ignored in git)
├── pom.xml                                    # Maven dependencies
├── .gitignore                                 # Git ignore rules
└── README.md                                  # This file
```

## 🗄️ Database Schema

The application uses 11 interconnected tables:

### Core Tables
- **USER** - User accounts with hashed passwords
- **BOOK** - Book information (title, language, format, file path)
- **AUTHOR** - Book authors
- **GENRE** - Book genres/categories
- **PUBLISHER** - Book publishers

### Relationship Tables
- **USERBOOK** - User's library (books added, progress, ratings, reading status)
- **COLLECTION** - User-created collections
- **BOOKMARK** - User bookmarks with page numbers and notes

### Junction Tables
- **AUTHORBOOK** - Many-to-many: Authors ↔ Books
- **GENREBOOK** - Many-to-many: Genres ↔ Books
- **COLLECTIONBOOK** - Many-to-many: Collections ↔ Books

## 🌐 API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

### Books
- `GET /api/books` - Get all books
- `GET /api/books/{id}` - Get book by ID
- `GET /api/books/{id}/details` - Get detailed book info with authors, genres
- `GET /api/books/search?q={query}` - Search books
- `GET /api/books/read/{id}` - Read book (PDF/EPUB stream)
- `POST /api/books` - Upload new book (multipart/form-data)
- `PUT /api/books/{id}` - Update book details
- `DELETE /api/books/{id}` - Delete book
- `GET /api/books/my-uploads` - Get user's uploaded books
- `GET /api/books/{id}/rating` - Get book's average rating

### User Library
- `GET /api/library` - Get user's library
- `POST /api/library` - Add book to library
- `PUT /api/library/{bookId}` - Update reading progress/rating
- `DELETE /api/library/{bookId}` - Remove book from library
- `GET /api/library/hot-books` - Get most popular books

### Collections
- `GET /api/collections` - Get user's collections
- `POST /api/collections` - Create new collection
- `GET /api/collections/{id}/books` - Get books in collection
- `POST /api/collections/{id}/books/{bookId}` - Add book to collection
- `DELETE /api/collections/{id}/books/{bookId}` - Remove book from collection
- `DELETE /api/collections/{id}` - Delete collection

### Bookmarks
- `GET /api/bookmarks/book/{bookId}` - Get bookmarks for a book
- `POST /api/bookmarks` - Create bookmark
- `PUT /api/bookmarks/{id}` - Update bookmark
- `DELETE /api/bookmarks/{id}` - Delete bookmark

### Authors, Genres, Publishers
- `GET /api/authors` - Get all authors
- `GET /api/genres` - Get all genres
- `GET /api/publishers` - Get all publishers
- `GET /api/community/books` - Get community books with details

All endpoints except authentication and public book browsing require a valid JWT token in the `Authorization` header:
```
Authorization: Bearer <your-jwt-token>
```

All endpoints except authentication and public book browsing require a valid JWT token in the `Authorization` header:
```
Authorization: Bearer <your-jwt-token>
```

## 🎨 User Interface

### Pages

1. **Landing Page** (`index.html`)
   - Welcome page with app overview
   - Links to login/register
   - Featured books showcase

2. **Authentication**
   - `login.html` - User login form
   - `register.html` - New user registration

3. **Dashboard** (`dashboard.html`)
   - Main navigation hub
   - Quick stats (total books, collections, bookmarks)
   - Recent activity

4. **Library** (`library.html`)
   - Personal book collection
   - Filter by reading status (Reading, Completed, Want to Read)
   - Sort by title, date added, rating
   - Update reading progress
   - Rate books (1-5 stars)

5. **Community** (`community.html`)
   - Browse all available books
   - Search and filter functionality
   - Add books to your library

6. **Upload Book** (`upload.html`)
   - Upload PDF/EPUB files
   - Add book metadata (title, author, publisher, genres)
   - Drag-and-drop support

7. **My Uploads** (`my-uploads.html`)
   - Manage your uploaded books
   - Edit book details
   - Delete books

8. **Reader** (`reader.html`)
   - In-browser PDF reader
   - Page navigation
   - Zoom controls
   - Bookmark current page
   - Add notes to bookmarks

## 🔧 Development

### Project Setup for Development

```bash
# Clone repository
git clone <your-repo-url>
cd book-library-webapp

# Install dependencies and build
mvn clean install

# Run in development mode (with hot reload)
mvn spring-boot:run
```

### Development Tools

- **Spring Boot DevTools** - Automatic restart on code changes
- **Maven** - Build automation and dependency management
- **VS Code/IntelliJ IDEA** - Recommended IDEs

### Building for Production

```bash
# Create production JAR
mvn clean package -DskipTests

# The JAR will be created at:
# target/book-library-webapp-2.0.0.jar

# Run in production
java -jar target/book-library-webapp-2.0.0.jar
```

### Configuration Files

**application.properties** - Main configuration
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/book_library
spring.datasource.username=root
spring.datasource.password=your_password

# JWT
jwt.secret=your-secret-key-here
jwt.expiration=86400000

# File Upload
file.upload-dir=./uploads
spring.servlet.multipart.max-file-size=50MB
```

## � Security Features

### Authentication Flow
1. User registers with username and password
2. Password is hashed using BCrypt (cost factor 10)
3. User logs in with credentials
4. Server validates credentials and issues JWT token
5. Client stores token in localStorage
6. Token is sent with each request in Authorization header
7. Server validates token and extracts user information

### JWT Token
- **Algorithm**: HS512
- **Expiration**: 24 hours (configurable)
- **Claims**: userId, username, issued-at, expiration
- **Secret**: Configured in application.properties (change in production!)

### Endpoint Protection
- Public endpoints: Login, Register, Browse books, Read books
- Protected endpoints: Library management, Upload, Collections, Bookmarks
- Ownership validation: Users can only modify their own data

### Password Security
- **BCrypt hashing** with salt
- **Minimum length**: Not enforced (add validation as needed)
- **Never stored in plain text**

## 📊 Sample Data

The `database.sql` file includes sample data:

- **3 Users** - `an_nguyen`, `binh_le`, `chi_tran` (password: `password123`)
- **10 Books** - Various genres and formats
- **8 Authors** - Well-known authors
- **6 Genres** - Fiction, Science, History, Technology, Philosophy, Biography
- **5 Publishers** - Major publishing houses
- **User Libraries** - Pre-populated reading lists
- **3 Collections** - Sample book collections
- **5 Bookmarks** - Example bookmarks with notes

## 🐛 Troubleshooting

### Database Connection Issues

**Error: "Access denied for user 'root'@'localhost'"**
- Check MySQL username/password in `application.properties`
- Ensure MySQL server is running: `mysql.server status`

**Error: "Unknown database 'book_library'"**
- Run the database setup script: `mysql -u root -p < src/main/resources/database.sql`

### File Upload Issues

**Error: "Could not create upload directory"**
- Check file.upload-dir path in application.properties
- Ensure application has write permissions
- Create directory manually: `mkdir uploads`

**Error: "File size exceeds maximum"**
- Files must be under 50MB
- Adjust in application.properties: `spring.servlet.multipart.max-file-size=100MB`

### Authentication Issues

**Error: "Invalid JWT token"**
- Token may have expired (24-hour lifetime)
- Login again to get a new token
- Check jwt.secret is the same in application.properties

**Error: "403 Forbidden"**
- Ensure Authorization header is set: `Bearer <token>`
- Check that you own the resource you're trying to modify

### Build Issues

**Error: "Source option 5 is no longer supported"**
- Ensure Java 17+ is installed: `java -version`
- Update JAVA_HOME environment variable

**Error: "Could not resolve dependencies"**
- Run: `mvn clean install -U`
- Check internet connection
- Clear Maven cache: `rm -rf ~/.m2/repository`

## 🚀 Deployment

### Local Deployment

```bash
# Build
mvn clean package

# Run
java -jar target/book-library-webapp-2.0.0.jar
```

### Production Deployment

1. **Update configuration** for production:
   ```properties
   # Use strong JWT secret
   jwt.secret=<generate-strong-random-secret>
   
   # Use production database
   spring.datasource.url=jdbc:mysql://prod-server:3306/book_library
   spring.datasource.username=prod_user
   spring.datasource.password=<strong-password>
   
   # Configure upload directory
   file.upload-dir=/var/lib/booklibrary/uploads
   ```

2. **Build production JAR**:
   ```bash
   mvn clean package -DskipTests
   ```

3. **Run as a service** (Linux):
   ```bash
   # Create systemd service file
   sudo nano /etc/systemd/system/booklibrary.service
   ```
   
   ```ini
   [Unit]
   Description=Book Library Web Application
   After=mysql.service
   
   [Service]
   Type=simple
   User=booklibrary
   ExecStart=/usr/bin/java -jar /opt/booklibrary/book-library-webapp-2.0.0.jar
   Restart=on-failure
   
   [Install]
   WantedBy=multi-user.target
   ```
   
   ```bash
   # Enable and start service
   sudo systemctl enable booklibrary
   sudo systemctl start booklibrary
   ```

### Docker Deployment (Optional)

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/book-library-webapp-2.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# Build image
docker build -t book-library-webapp .

# Run container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/book_library \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  -v ./uploads:/app/uploads \
  book-library-webapp
```

## 🧪 Testing

### Manual Testing

Use the included sample users:
- Username: `an_nguyen`, Password: `password123`
- Username: `binh_le`, Password: `password123`
- Username: `chi_tran`, Password: `password123`

### API Testing with cURL

**Register**:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"testpass123"}'
```

**Login**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"an_nguyen","password":"password123"}'
```

**Get Books** (with authentication):
```bash
curl -X GET http://localhost:8080/api/books \
  -H "Authorization: Bearer <your-jwt-token>"
```

**Upload Book**:
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Authorization: Bearer <your-jwt-token>" \
  -F "title=My Book" \
  -F "author=John Doe" \
  -F "publisher=Publisher Name" \
  -F "language=English" \
  -F "genreIds=[1,2]" \
  -F "ebookContent=@/path/to/book.pdf"
```

## 🎯 Future Enhancements

### Planned Features
- [ ] EPUB reader support (currently PDF only)
- [ ] Social features (comments, reviews, book clubs)
- [ ] Reading statistics and analytics
- [ ] Book recommendations based on reading history
- [ ] Export/import library data
- [ ] Mobile responsive design improvements
- [ ] Dark mode
- [ ] Email notifications
- [ ] Advanced search filters
- [ ] Book series tracking

### Technical Improvements
- [ ] Add unit and integration tests
- [ ] Implement caching (Redis)
- [ ] Add API documentation (Swagger/OpenAPI)
- [ ] Implement rate limiting
- [ ] Add logging and monitoring
- [ ] Database migration tool (Flyway/Liquibase)
- [ ] Containerization (Docker Compose)
- [ ] CI/CD pipeline

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Style
- Follow Java naming conventions
- Use meaningful variable names
- Add comments for complex logic
- Keep methods focused and concise

## 📄 License

This project is open source and available under the MIT License.

## 👨‍💻 Author

Created as a full-stack web development demonstration project.

## 📞 Support

For issues, questions, or suggestions:
- Open an issue on GitHub
- Check existing issues for solutions
- Review the troubleshooting section

## 🙏 Acknowledgments

- Spring Boot for the excellent framework
- PDF.js for client-side PDF rendering
- Apache PDFBox for server-side PDF processing
- MySQL for reliable data storage
- All open-source contributors
