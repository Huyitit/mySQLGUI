# 📊 Database Flow Documentation (DAO Layer)

This document traces the complete flow from frontend to database for all major features in the Book Library Web Application.

## 🔐 USER OPERATIONS

### 1. Register (INSERT)
**Flow:**
```
register.html (Frontend)
    ↓ [User fills form: username, password, confirmPassword]
auth.js → fetch POST /api/auth/register
    ↓ [Sends JSON: {username, password}]
AuthController.register(@RequestBody RegisterRequest)
    ↓ [Receives request]
AuthService.register(request)
    ↓ [Validates username not exists, hashes password]
UserDAO.findByUsername(username)
    → SQL: SELECT * FROM USER WHERE UserName = ?
    ↓ [Returns null if user doesn't exist]
UserDAO.insert(user)
    → SQL: INSERT INTO USER (UserName, HashedPassword) VALUES (?, ?)
    ↓ [Returns generated UserId]
AuthService returns AuthResponse(null, userId, username)
    ↓ [No token - user must login]
Frontend: Redirect to login.html
```

**Database Queries:**
- `SELECT * FROM USER WHERE UserName = ?` - Check if username exists
- `INSERT INTO USER (UserName, HashedPassword) VALUES (?, ?)` - Create new user

---

### 2. Login (SELECT)
**Flow:**
```
login.html (Frontend)
    ↓ [User enters username, password]
auth.js → fetch POST /api/auth/login
    ↓ [Sends JSON: {username, password}]
AuthController.login(@RequestBody LoginRequest)
    ↓
AuthService.login(request)
    ↓ [Spring Security authentication]
AuthenticationManager.authenticate()
    ↓
UserDetailsServiceImpl.loadUserByUsername(username)
    ↓
UserDAO.findByUsername(username)
    → SQL: SELECT * FROM USER WHERE UserName = ?
    ↓ [Returns User object]
PasswordHasher.verifyPassword(inputPassword, hashedPassword)
    ↓ [BCrypt verification]
JwtTokenProvider.generateToken(authentication)
    ↓ [Creates JWT token]
AuthService returns AuthResponse(token, userId, username)
    ↓
Frontend: Store token, userId, username in localStorage
Frontend: Redirect to dashboard.html
```

**Database Queries:**
- `SELECT * FROM USER WHERE UserName = ?` - Find user for authentication

---

### 3. Find User by ID (SELECT)
**Flow:**
```
UserDAO.findById(userId)
    → SQL: SELECT * FROM USER WHERE UserId = ?
    ↓ [Returns User object or null]
```

**Database Queries:**
- `SELECT * FROM USER WHERE UserId = ?`

---

### 4. Delete User Account (DELETE)
**Flow:**
```
User settings page (Frontend)
    ↓ [User clicks "Delete My Account"]
app.js → fetch DELETE /api/users/me
    ↓ [With Authorization header: Bearer {token}]
UserController.deleteMyAccount()
    ↓ [Get userId from SecurityContext]
UserDAO.deleteById(userId)
    → SQL: DELETE FROM USER WHERE UserId = ?
    ↓ [CASCADE deletes all user data]
Frontend: Clear localStorage and redirect to index.html
```

**Database Queries:**
- `DELETE FROM USER WHERE UserId = ?`

**Note:** Database CASCADE DELETE automatically removes:
- All user's books in `USERBOOK`
- All user's collections in `COLLECTION`
- All user's bookmarks in `BOOKMARK` (via USERBOOK cascade)
- All books uploaded by user in `BOOK`

---

## 📚 BOOK OPERATIONS

### 1. Upload Book (INSERT + Relations)
**Flow:**
```
upload.html (Frontend)
    ↓ [User fills form: title, author, publisher, language, genres, PDF file]
upload.js → Autocomplete requests:
    ├─ fetch GET /api/authors/search?q={query}
    │   ↓
    │   AuthorController.searchAuthors(query)
    │       ↓
    │       AuthorDAO.searchByName(query)
    │           → SQL: SELECT * FROM AUTHOR WHERE AuthorName LIKE ? ORDER BY AuthorName LIMIT 10
    │
    ├─ fetch GET /api/publishers/search?q={query}
    │   ↓
    │   PublisherController.searchPublishers(query)
    │       ↓
    │       PublisherDAO.searchByName(query)
    │           → SQL: SELECT * FROM PUBLISHER WHERE PublisherName LIKE ? ORDER BY PublisherName LIMIT 10
    │
    └─ fetch GET /api/genres (load all genres)
        ↓
        GenreController.getAllGenres()
            ↓
            GenreDAO.findAll()
                → SQL: SELECT * FROM GENRE ORDER BY GenreName

upload.js → fetch POST /api/books (multipart/form-data)
    ↓ [Sends: title, author, publisher, language, genreIds, ebookContent file]
BookController.uploadBook(@RequestParam + @RequestParam("ebookContent") MultipartFile)
    ↓ [Validates file, user authentication]
FileStorageService.storeFile(file)
    ↓ [Saves file to uploads/ directory, returns filename]
PublisherDAO.findOrCreateByName(publisherName)
    → SQL: SELECT * FROM PUBLISHER WHERE PublisherName = ?
    ↓ [If not exists:]
    → SQL: INSERT INTO PUBLISHER (PublisherName) VALUES (?)
    ↓ [Returns Publisher with ID]
BookDAO.addBook(book, uploaderId)
    → SQL: INSERT INTO BOOK (Name, Language, Format, FilePath, UserId, PublisherId) 
           VALUES (?, ?, ?, ?, ?, ?)
    ↓ [Returns generated BookId]
AuthorDAO.findOrCreateByName(authorName)
    → SQL: SELECT * FROM AUTHOR WHERE AuthorName = ?
    ↓ [If not exists:]
    → SQL: INSERT INTO AUTHOR (AuthorName) VALUES (?)
    ↓ [Returns Author with ID]
BookDAO.addAuthorToBook(bookId, authorId)
    → SQL: INSERT IGNORE INTO AUTHORBOOK (AuthorId, BookId) VALUES (?, ?)
BookDAO.addGenresToBook(bookId, genreIds)
    → SQL: INSERT IGNORE INTO GENREBOOK (GenreId, BookId) VALUES (?, ?)
    ↓ [Batch insert for multiple genres]
Frontend: Redirect to dashboard.html or library.html
```

**Database Queries:**
- `SELECT * FROM AUTHOR WHERE AuthorName LIKE ?` - Search authors (autocomplete)
- `SELECT * FROM PUBLISHER WHERE PublisherName LIKE ?` - Search publishers (autocomplete)
- `SELECT * FROM GENRE ORDER BY GenreName` - Load all genres
- `SELECT * FROM PUBLISHER WHERE PublisherName = ?` - Find/check publisher
- `INSERT INTO PUBLISHER (PublisherName) VALUES (?)` - Create publisher if needed
- `INSERT INTO BOOK (Name, Language, Format, FilePath, UserId, PublisherId) VALUES (?, ?, ?, ?, ?, ?)` - Insert book
- `SELECT * FROM AUTHOR WHERE AuthorName = ?` - Find/check author
- `INSERT INTO AUTHOR (AuthorName) VALUES (?)` - Create author if needed
- `INSERT IGNORE INTO AUTHORBOOK (AuthorId, BookId) VALUES (?, ?)` - Link author to book
- `INSERT IGNORE INTO GENREBOOK (GenreId, BookId) VALUES (?, ?)` - Link genres to book (batch)

---

### 2. View All Books (SELECT)
**Flow:**
```
community.html or library.html (Frontend)
    ↓
app.js → fetch GET /api/books
    ↓ [With Authorization header: Bearer {token}]
BookController.getAllBooks()
    ↓
BookDAO.getAllBooks()
    → SQL: SELECT BookId, Name, Language, Format, FilePath, UserId, PublisherId 
           FROM BOOK ORDER BY Name
    ↓ [Returns List<Book>]
Frontend: Display books in grid/list
```

**Database Queries:**
- `SELECT BookId, Name, Language, Format, FilePath, UserId, PublisherId FROM BOOK ORDER BY Name`

---

### 3. View Book Details (SELECT with JOINs)
**Flow:**
```
User clicks on book in frontend
    ↓
app.js → fetch GET /api/books/{id}/details
    ↓
BookController.getBookDetails(id, userPrincipal)
    ↓
BookDAO.getBookDetails(bookId)
    → SQL: SELECT b.BookId, b.Name, b.Language, b.Format, b.FilePath,
                  p.PublisherName,
                  GROUP_CONCAT(DISTINCT a.AuthorName SEPARATOR ', ') AS Authors,
                  GROUP_CONCAT(DISTINCT g.GenreName SEPARATOR ', ') AS Genres
           FROM BOOK b
           LEFT JOIN PUBLISHER p ON b.PublisherId = p.PublisherId
           LEFT JOIN AUTHORBOOK ab ON b.BookId = ab.BookId
           LEFT JOIN AUTHOR a ON ab.AuthorId = a.AuthorId
           LEFT JOIN GENREBOOK gb ON b.BookId = gb.BookId
           LEFT JOIN GENRE g ON gb.GenreId = g.GenreId
           WHERE b.BookId = ?
           GROUP BY b.BookId, b.Name, b.Language, b.Format, b.FilePath, p.PublisherName
    ↓ [Returns Map with all details]
Frontend: Display book details modal/page
```

**Database Queries:**
- Complex JOIN query to get book with all related data (authors, genres, publisher)

---

### 4. Search Books (SELECT with LIKE)
**Flow:**
```
User types in search box
    ↓
app.js → fetch GET /api/books/search?q={query}
    ↓
BookController.searchBooks(query)
    ↓
BookDAO.searchBooks(query)
    → SQL: SELECT BookId, Name, Language, Format, FilePath, UserId, PublisherId
           FROM BOOK WHERE Name LIKE ? ORDER BY Name
    ↓ [Returns List<Book> matching query]
Frontend: Display search results
```

**Database Queries:**
- `SELECT BookId, Name, Language, Format, FilePath, UserId, PublisherId FROM BOOK WHERE Name LIKE ? ORDER BY Name`

---

### 5. Update Book (UPDATE + Relations)
**Flow:**
```
dashboard.html or upload.html (Frontend)
    ↓ [User clicks edit on their uploaded book, modifies: title, author, publisher, language, genres]
app.js → fetch PUT /api/books/{id}
    ↓ [Sends: title, author, publisher, language, genreIds]
BookController.updateBook(id, params, userPrincipal)
    ↓ [Check ownership: book.UserId == currentUser.UserId]
BookDAO.getBookById(id)
    → SQL: SELECT BookId, Name, Language, Format, FilePath, UserId, PublisherId
           FROM BOOK WHERE BookId = ?
    ↓ [Verify ownership]
PublisherDAO.findOrCreateByName(publisherName)
    → SQL: SELECT * FROM PUBLISHER WHERE PublisherName = ?
    ↓ [Create if not exists]
BookDAO.updateBook(book, userId)
    → SQL: UPDATE BOOK SET Name = ?, Language = ?, Format = ?, PublisherId = ?
           WHERE BookId = ? AND UserId = ?
BookDAO.removeAuthorsFromBook(bookId)
    → SQL: DELETE FROM AUTHORBOOK WHERE BookId = ?
AuthorDAO.findOrCreateByName(authorName)
    → SQL: SELECT * FROM AUTHOR WHERE AuthorName = ?
    ↓ [Create if needed]
BookDAO.addAuthorToBook(bookId, authorId)
    → SQL: INSERT IGNORE INTO AUTHORBOOK (AuthorId, BookId) VALUES (?, ?)
BookDAO.removeGenresFromBook(bookId)
    → SQL: DELETE FROM GENREBOOK WHERE BookId = ?
BookDAO.addGenresToBook(bookId, genreIds)
    → SQL: INSERT IGNORE INTO GENREBOOK (GenreId, BookId) VALUES (?, ?)
Frontend: Refresh book list
```

**Database Queries:**
- `SELECT BookId, Name, Language, Format, FilePath, UserId, PublisherId FROM BOOK WHERE BookId = ?` - Get book
- `SELECT * FROM PUBLISHER WHERE PublisherName = ?` - Find publisher
- `UPDATE BOOK SET Name = ?, Language = ?, Format = ?, PublisherId = ? WHERE BookId = ? AND UserId = ?` - Update book
- `DELETE FROM AUTHORBOOK WHERE BookId = ?` - Remove old authors
- `SELECT * FROM AUTHOR WHERE AuthorName = ?` - Find author
- `INSERT IGNORE INTO AUTHORBOOK (AuthorId, BookId) VALUES (?, ?)` - Add new author
- `DELETE FROM GENREBOOK WHERE BookId = ?` - Remove old genres
- `INSERT IGNORE INTO GENREBOOK (GenreId, BookId) VALUES (?, ?)` - Add new genres (batch)

---

### 6. Delete Book (DELETE + Cascades)
**Flow:**
```
dashboard.html or upload.html (Frontend)
    ↓ [User clicks delete button on their uploaded book]
app.js → fetch DELETE /api/books/{id}
    ↓ [With Authorization header]
BookController.deleteBook(id, userPrincipal)
    ↓ [Check ownership]
BookDAO.getBookById(id)
    → SQL: SELECT BookId, Name, Language, Format, FilePath, UserId, PublisherId
           FROM BOOK WHERE BookId = ?
    ↓ [Verify book.UserId == currentUser.UserId]
BookDAO.deleteBook(bookId, userId)
    → SQL: DELETE FROM BOOK WHERE BookId = ? AND UserId = ?
    ↓ [CASCADE deletes from AUTHORBOOK, GENREBOOK, COLLECTIONBOOK, USERBOOK, BOOKMARK]
FileStorageService.deleteFile(filename)
    ↓ [Delete physical file from uploads/]
Frontend: Remove book from UI
```

**Database Queries:**
- `SELECT BookId, Name, Language, Format, FilePath, UserId, PublisherId FROM BOOK WHERE BookId = ?` - Get book for ownership check
- `DELETE FROM BOOK WHERE BookId = ? AND UserId = ?` - Delete book (cascades to related tables)

**Note:** Database CASCADE DELETE automatically removes:
- Related records in `AUTHORBOOK`
- Related records in `GENREBOOK`
- Related records in `COLLECTIONBOOK`
- Related records in `USERBOOK`
- Related records in `BOOKMARK` (via USERBOOK cascade)

---

### 7. Get My Uploads (SELECT with JOINs)
**Flow:**
```
dashboard.html or upload.html (Frontend)
    ↓ [User navigates to "My Uploads" section]
app.js → fetch GET /api/books/my-uploads
    ↓ [With Authorization header]
BookController.getMyUploads(userPrincipal)
    ↓ [Extract userId from JWT token]
BookDAO.getBooksByUploader(userId)
    → SQL: SELECT b.BookId, b.Name AS title,
                  GROUP_CONCAT(DISTINCT a.AuthorName SEPARATOR ', ') AS authors,
                  p.PublisherName AS publisher,
                  GROUP_CONCAT(DISTINCT g.GenreName SEPARATOR ', ') AS genres,
                  b.Language, b.PublishedDate AS publishedDate, b.Format
           FROM BOOK b
           LEFT JOIN AUTHORBOOK ab ON b.BookId = ab.BookId
           LEFT JOIN AUTHOR a ON ab.AuthorId = a.AuthorId
           LEFT JOIN PUBLISHER p ON b.PublisherId = p.PublisherId
           LEFT JOIN GENREBOOK gb ON b.BookId = gb.BookId
           LEFT JOIN GENRE g ON gb.GenreId = g.GenreId
           WHERE b.UserId = ?
           GROUP BY b.BookId, b.Name, p.PublisherName, b.Language, b.PublishedDate, b.Format
           ORDER BY b.BookId DESC
    ↓ [Returns List<Map> with book details]
Frontend: Display user's uploaded books with edit/delete options
```

**Database Queries:**
- Complex JOIN query to get all books uploaded by user with full details

---

### 8. Read Book (SELECT + File Stream)
**Flow:**
```
User clicks "Read" button
    ↓
reader.html → fetch GET /api/books/read/{id}
    ↓
BookController.readBook(id)
    ↓
BookDAO.getBookById(id)
    → SQL: SELECT BookId, Name, Language, Format, FilePath, UserId, PublisherId
           FROM BOOK WHERE BookId = ?
    ↓ [Get file path from database]
FileStorageService.loadFileAsResource(filename)
    ↓ [Load file from uploads/ directory]
Return: PDF/EPUB file stream (Content-Type: application/pdf)
    ↓
Frontend: PDF.js renders PDF in browser
```

**Database Queries:**
- `SELECT BookId, Name, Language, Format, FilePath, UserId, PublisherId FROM BOOK WHERE BookId = ?`

---

## 📖 USER LIBRARY OPERATIONS

### 1. View My Library (SELECT with Complex JOINs)
**Flow:**
```
library.html (Frontend)
    ↓
app.js → fetch GET /api/library
    ↓ [With Authorization header]
UserBookController.getUserLibrary(userPrincipal)
    ↓ [Extract username from JWT]
UserBookDAO.getUserLibraryWithDetails(username)
    → SQL: SELECT b.BookId, b.Name AS BookTitle, b.Language, b.Format,
                  GROUP_CONCAT(DISTINCT a.AuthorName SEPARATOR ', ') AS Authors,
                  ub.Progress, ub.UserRating, ub.LastReadDate
           FROM USERBOOK ub
           JOIN BOOK b ON ub.BookId = b.BookId
           JOIN USER u ON ub.UserId = u.UserId
           LEFT JOIN AUTHORBOOK ab ON b.BookId = ab.BookId
           LEFT JOIN AUTHOR a ON ab.AuthorId = a.AuthorId
           WHERE u.UserName = ?
           GROUP BY b.BookId, b.Name, b.Language, b.Format, ub.Progress, ub.UserRating, ub.LastReadDate
           ORDER BY ub.LastReadDate DESC
    ↓ [Returns List<Map> with book + user data]
Frontend: Display user's library with progress, ratings, last read date
```

**Database Queries:**
- Complex JOIN query across USERBOOK, BOOK, USER, AUTHORBOOK, AUTHOR tables

---

### 2. Add Book to Library (INSERT)
**Flow:**
```
User clicks "Add to Library" button
    ↓
app.js → fetch POST /api/library
    ↓ [Sends JSON: {bookId, progress: "Want to Read"}]
UserBookController.addBookToLibrary(request, userPrincipal)
    ↓ [Extract userId from JWT]
UserBookDAO.addUserBook(userBook)
    → SQL: SELECT COUNT(*) FROM USERBOOK WHERE UserId = ? AND BookId = ?
    ↓ [Check if already in library]
    → SQL: INSERT INTO USERBOOK (UserId, BookId, Progress, AddedDate, UserRating)
           VALUES (?, ?, ?, ?, ?)
Frontend: Update UI, show "In Library" status
```

**Database Queries:**
- `SELECT COUNT(*) FROM USERBOOK WHERE UserId = ? AND BookId = ?` - Check if book already in library
- `INSERT INTO USERBOOK (UserId, BookId, Progress, AddedDate, UserRating) VALUES (?, ?, ?, ?, ?)` - Add to library

---

### 3. Update Reading Progress (UPDATE)
**Flow:**
```
User updates progress dropdown (Want to Read → Reading → Completed)
    ↓
app.js → fetch PUT /api/library/{bookId}
    ↓ [Sends JSON: {progress: "Reading"}]
UserBookController.updateProgress(bookId, progress, userPrincipal)
    ↓
UserBookDAO.updateProgress(userId, bookId, progress)
    → SQL: UPDATE USERBOOK SET Progress = ?, LastReadDate = ?
           WHERE UserId = ? AND BookId = ?
Frontend: Update progress badge
```

**Database Queries:**
- `UPDATE USERBOOK SET Progress = ?, LastReadDate = ? WHERE UserId = ? AND BookId = ?`

---

### 4. Update Rating (UPDATE)
**Flow:**
```
User clicks star rating (1-5 stars)
    ↓
app.js → fetch PUT /api/library/{bookId}
    ↓ [Sends JSON: {rating: 4}]
UserBookController.updateRating(bookId, rating, userPrincipal)
    ↓
UserBookDAO.updateRating(userId, bookId, rating)
    → SQL: UPDATE USERBOOK SET UserRating = ? WHERE UserId = ? AND BookId = ?
Frontend: Update star display
```

**Database Queries:**
- `UPDATE USERBOOK SET UserRating = ? WHERE UserId = ? AND BookId = ?`

---

### 5. Remove from Library (DELETE)
**Flow:**
```
User clicks "Remove from Library"
    ↓
app.js → fetch DELETE /api/library/{bookId}
    ↓
UserBookController.removeFromLibrary(bookId, userPrincipal)
    ↓
UserBookDAO.removeFromLibrary(userId, bookId)
    → SQL: DELETE FROM USERBOOK WHERE UserId = ? AND BookId = ?
    ↓ [CASCADE deletes related bookmarks]
Frontend: Remove book from library view
```

**Database Queries:**
- `DELETE FROM USERBOOK WHERE UserId = ? AND BookId = ?` - Cascades to BOOKMARK table

---

### 6. Get Hot Books (SELECT with Aggregation)
**Flow:**
```
dashboard.html (Frontend)
    ↓
app.js → fetch GET /api/library/hot-books
    ↓
UserBookController.getHotBooks()
    ↓
UserBookDAO.getTopPopularBooks(limit = 5)
    → SQL: SELECT b.Name AS BookTitle,
                  GROUP_CONCAT(DISTINCT a.AuthorName SEPARATOR ', ') AS Authors,
                  COUNT(DISTINCT ub.UserId) AS TotalUsers,
                  AVG(ub.UserRating) AS AverageRating
           FROM USERBOOK ub
           JOIN BOOK b ON ub.BookId = b.BookId
           LEFT JOIN AUTHORBOOK ab ON b.BookId = ab.BookId
           LEFT JOIN AUTHOR a ON ab.AuthorId = a.AuthorId
           GROUP BY b.BookId, b.Name
           ORDER BY TotalUsers DESC
           LIMIT ?
Frontend: Display top 5 most popular books
```

**Database Queries:**
- Aggregation query with GROUP BY, COUNT, AVG to get most popular books

---

### 7. Get Book Rating (SELECT with Aggregation)
**Flow:**
```
app.js → fetch GET /api/books/{id}/rating
    ↓
BookController.getBookRating(bookId)
    ↓
UserBookDAO.getAverageRating(bookId)
    → SQL: SELECT AVG(ub.UserRating) AS AverageRating,
                  COUNT(ub.UserRating) AS TotalRatings
           FROM USERBOOK ub
           WHERE ub.BookId = ? AND ub.UserRating IS NOT NULL
Frontend: Display average rating (e.g., 4.2/5 stars from 15 ratings)
```

**Database Queries:**
- `SELECT AVG(ub.UserRating) AS AverageRating, COUNT(ub.UserRating) AS TotalRatings FROM USERBOOK ub WHERE ub.BookId = ? AND ub.UserRating IS NOT NULL`

---

## 🗂️ COLLECTION OPERATIONS

### 1. View My Collections (SELECT with COUNT)
**Flow:**
```
dashboard.html or collections page
    ↓
app.js → fetch GET /api/collections
    ↓
CollectionController.getUserCollections(userPrincipal)
    ↓
CollectionDAO.getCollectionsWithBookCount(userId)
    → SQL: SELECT c.CollectionId, c.CollectionName, c.CreatedDate,
                  COUNT(cb.BookId) as BookCount
           FROM COLLECTION c
           LEFT JOIN COLLECTIONBOOK cb ON c.CollectionId = cb.CollectionId
           WHERE c.UserId = ?
           GROUP BY c.CollectionId, c.CollectionName, c.CreatedDate
           ORDER BY c.CreatedDate DESC
Frontend: Display collections with book counts
```

**Database Queries:**
- `SELECT c.CollectionId, c.CollectionName, c.CreatedDate, COUNT(cb.BookId) as BookCount FROM COLLECTION c LEFT JOIN COLLECTIONBOOK cb ON c.CollectionId = cb.CollectionId WHERE c.UserId = ? GROUP BY ... ORDER BY c.CreatedDate DESC`

---

### 2. Create Collection (INSERT)
**Flow:**
```
User clicks "Create Collection"
    ↓
app.js → fetch POST /api/collections
    ↓ [Sends JSON: {collectionName: "My Favorites"}]
CollectionController.createCollection(request, userPrincipal)
    ↓
CollectionDAO.createCollection(collectionName, userId)
    → SQL: INSERT INTO COLLECTION (CollectionName, UserId, CreatedDate)
           VALUES (?, ?, ?)
    ↓ [Returns generated CollectionId]
Frontend: Add new collection to UI
```

**Database Queries:**
- `INSERT INTO COLLECTION (CollectionName, UserId, CreatedDate) VALUES (?, ?, ?)`

---

### 3. View Books in Collection (SELECT with JOINs)
**Flow:**
```
User clicks on a collection
    ↓
app.js → fetch GET /api/collections/{collectionId}/books
    ↓
CollectionController.getBooksInCollection(collectionId, userPrincipal)
    ↓ [Verify collection belongs to user]
CollectionDAO.getBooksInCollection(collectionId)
    → SQL: SELECT b.BookId, b.Name, b.Language, b.Format,
                  GROUP_CONCAT(DISTINCT a.AuthorName SEPARATOR ', ') AS Authors
           FROM BOOK b
           JOIN COLLECTIONBOOK cb ON b.BookId = cb.BookId
           LEFT JOIN AUTHORBOOK ab ON b.BookId = ab.BookId
           LEFT JOIN AUTHOR a ON ab.AuthorId = a.AuthorId
           WHERE cb.CollectionId = ?
           GROUP BY b.BookId, b.Name, b.Language, b.Format
Frontend: Display books in collection
```

**Database Queries:**
- JOIN query across BOOK, COLLECTIONBOOK, AUTHORBOOK, AUTHOR tables

---

### 4. Add Book to Collection (INSERT)
**Flow:**
```
User clicks "Add to Collection" → selects collection
    ↓
app.js → fetch POST /api/collections/{collectionId}/books/{bookId}
    ↓
CollectionController.addBookToCollection(collectionId, bookId, userPrincipal)
    ↓
CollectionDAO.addBookToCollection(collectionId, bookId)
    → SQL: INSERT IGNORE INTO COLLECTIONBOOK (CollectionId, BookId)
           VALUES (?, ?)
Frontend: Update collection book count
```

**Database Queries:**
- `INSERT IGNORE INTO COLLECTIONBOOK (CollectionId, BookId) VALUES (?, ?)` - INSERT IGNORE prevents duplicate errors

---

### 5. Remove Book from Collection (DELETE)
**Flow:**
```
User clicks "Remove from Collection"
    ↓
app.js → fetch DELETE /api/collections/{collectionId}/books/{bookId}
    ↓
CollectionController.removeBookFromCollection(collectionId, bookId, userPrincipal)
    ↓
CollectionDAO.removeBookFromCollection(collectionId, bookId)
    → SQL: DELETE FROM COLLECTIONBOOK
           WHERE CollectionId = ? AND BookId = ?
Frontend: Remove book from collection view
```

**Database Queries:**
- `DELETE FROM COLLECTIONBOOK WHERE CollectionId = ? AND BookId = ?`

---

### 6. Delete Collection (DELETE)
**Flow:**
```
User clicks "Delete Collection"
    ↓
app.js → fetch DELETE /api/collections/{collectionId}
    ↓
CollectionController.deleteCollection(collectionId, userPrincipal)
    ↓
CollectionDAO.deleteCollection(collectionId, userId)
    → SQL: DELETE FROM COLLECTION WHERE CollectionId = ? AND UserId = ?
    ↓ [CASCADE deletes from COLLECTIONBOOK]
Frontend: Remove collection from UI
```

**Database Queries:**
- `DELETE FROM COLLECTION WHERE CollectionId = ? AND UserId = ?` - Cascades to COLLECTIONBOOK

---

## 🔖 BOOKMARK OPERATIONS

### 1. View Bookmarks for Book (SELECT with JOINs)
**Flow:**
```
reader.html (Frontend) - User reading a book
    ↓
reader.js → fetch GET /api/bookmarks/book/{bookId}
    ↓
BookmarkController.getBookmarksForBook(bookId, userPrincipal)
    ↓
BookmarkDAO.getBookmarksByUserAndBook(userId, bookId)
    → SQL: SELECT bm.BookmarkId, bm.BookmarkName, bm.Location, bm.CreatedDate
           FROM BOOKMARK bm
           JOIN USERBOOK ub ON bm.UserBookId = ub.UserBookId
           WHERE ub.UserId = ? AND ub.BookId = ?
           ORDER BY bm.CreatedDate DESC
Frontend: Display bookmarks sidebar
```

**Database Queries:**
- `SELECT bm.BookmarkId, bm.BookmarkName, bm.Location, bm.CreatedDate FROM BOOKMARK bm JOIN USERBOOK ub ON bm.UserBookId = ub.UserBookId WHERE ub.UserId = ? AND ub.BookId = ? ORDER BY bm.CreatedDate DESC`

---

### 2. Add Bookmark (INSERT)
**Flow:**
```
User clicks "Bookmark This Page" in reader
    ↓
reader.js → fetch POST /api/bookmarks
    ↓ [Sends JSON: {bookId, bookmarkName: "Chapter 3", location: "Page 42"}]
BookmarkController.addBookmark(request, userPrincipal)
    ↓
BookmarkDAO.addBookmark(userId, bookId, bookmarkName, location)
    ↓
    → SQL: SELECT UserBookId FROM USERBOOK WHERE UserId = ? AND BookId = ?
    ↓ [Get UserBookId, or create USERBOOK entry if needed]
    → SQL: INSERT INTO USERBOOK (UserId, BookId, Progress, AddedDate)
           VALUES (?, ?, '0', ?) [If doesn't exist]
    ↓
    → SQL: INSERT INTO BOOKMARK (UserBookId, BookmarkName, Location, CreatedDate)
           VALUES (?, ?, ?, ?)
Frontend: Add bookmark to sidebar
```

**Database Queries:**
- `SELECT UserBookId FROM USERBOOK WHERE UserId = ? AND BookId = ?` - Get UserBookId
- `INSERT INTO USERBOOK (UserId, BookId, Progress, AddedDate) VALUES (?, ?, '0', ?)` - Create if needed
- `INSERT INTO BOOKMARK (UserBookId, BookmarkName, Location, CreatedDate) VALUES (?, ?, ?, ?)`

---

### 3. Update Bookmark (UPDATE)
**Flow:**
```
User edits bookmark name or notes
    ↓
reader.js → fetch PUT /api/bookmarks/{bookmarkId}
    ↓ [Sends JSON: {bookmarkName, location}]
BookmarkController.updateBookmark(bookmarkId, request, userPrincipal)
    ↓
BookmarkDAO.updateBookmark(bookmarkId, bookmarkName, location)
    → SQL: UPDATE BOOKMARK SET BookmarkName = ?, Location = ?
           WHERE BookmarkId = ?
Frontend: Update bookmark display
```

**Database Queries:**
- `UPDATE BOOKMARK SET BookmarkName = ?, Location = ? WHERE BookmarkId = ?`

---

### 4. Delete Bookmark (DELETE)
**Flow:**
```
User clicks delete on bookmark
    ↓
reader.js → fetch DELETE /api/bookmarks/{bookmarkId}
    ↓
BookmarkController.deleteBookmark(bookmarkId, userPrincipal)
    ↓
BookmarkDAO.deleteBookmark(bookmarkId)
    → SQL: DELETE FROM BOOKMARK WHERE BookmarkId = ?
Frontend: Remove bookmark from sidebar
```

**Database Queries:**
- `DELETE FROM BOOKMARK WHERE BookmarkId = ?`

---

## 🏷️ AUTHOR / GENRE / PUBLISHER OPERATIONS

### 1. Get All Authors (SELECT)
**Flow:**
```
AuthorController.getAllAuthors()
    ↓
AuthorDAO.findAll()
    → SQL: SELECT * FROM AUTHOR ORDER BY AuthorName
```

### 2. Search Authors (SELECT with LIKE)
**Flow:**
```
upload.js autocomplete → fetch GET /api/authors/search?q={query}
    ↓
AuthorController.searchAuthors(query)
    ↓
AuthorDAO.searchByName(query)
    → SQL: SELECT * FROM AUTHOR WHERE AuthorName LIKE ? ORDER BY AuthorName LIMIT 10
```

### 3. Get All Genres (SELECT)
**Flow:**
```
GenreController.getAllGenres()
    ↓
GenreDAO.findAll()
    → SQL: SELECT * FROM GENRE ORDER BY GenreName
```

### 4. Search Genres (SELECT with LIKE)
**Flow:**
```
GenreController.searchGenres(query)
    ↓
GenreDAO.searchByName(query)
    → SQL: SELECT * FROM GENRE WHERE GenreName LIKE ? ORDER BY GenreName LIMIT 10
```

### 5. Get All Publishers (SELECT)
**Flow:**
```
PublisherController.getAllPublishers()
    ↓
PublisherDAO.findAll()
    → SQL: SELECT * FROM PUBLISHER ORDER BY PublisherName
```

### 6. Search Publishers (SELECT with LIKE)
**Flow:**
```
PublisherController.searchPublishers(query)
    ↓
PublisherDAO.searchByName(query)
    → SQL: SELECT * FROM PUBLISHER WHERE PublisherName LIKE ? ORDER BY PublisherName LIMIT 10
```

---

## 📊 COMPLEX QUERIES (Original 7 Queries from Requirements)

### QUERY 1: My Library with Full Details
```sql
SELECT b.BookId, b.Name AS BookTitle, b.Language, b.Format,
       GROUP_CONCAT(DISTINCT a.AuthorName SEPARATOR ', ') AS Authors,
       ub.Progress, ub.UserRating, ub.LastReadDate
FROM USERBOOK ub
JOIN BOOK b ON ub.BookId = b.BookId
JOIN USER u ON ub.UserId = u.UserId
LEFT JOIN AUTHORBOOK ab ON b.BookId = ab.BookId
LEFT JOIN AUTHOR a ON ab.AuthorId = a.AuthorId
WHERE u.UserName = ?
GROUP BY b.BookId, b.Name, b.Language, b.Format, ub.Progress, ub.UserRating, ub.LastReadDate
ORDER BY ub.LastReadDate DESC
```

### QUERY 2: Books by Genre
```sql
SELECT b.Name AS BookTitle,
       GROUP_CONCAT(DISTINCT a.AuthorName SEPARATOR ', ') AS Authors,
       p.PublisherName, b.Language, b.Format
FROM BOOK b
JOIN GENREBOOK gb ON b.BookId = gb.BookId
JOIN GENRE g ON gb.GenreId = g.GenreId
LEFT JOIN AUTHORBOOK ab ON b.BookId = ab.BookId
LEFT JOIN AUTHOR a ON ab.AuthorId = a.AuthorId
LEFT JOIN PUBLISHER p ON b.PublisherId = p.PublisherId
WHERE g.GenreName = ?
GROUP BY b.BookId, b.Name, p.PublisherName, b.Language, b.Format
ORDER BY b.Name
```

### QUERY 3: User's Bookmarks for a Book
```sql
SELECT bm.BookmarkName, bm.Location, bm.CreatedDate
FROM BOOKMARK bm
JOIN USERBOOK ub ON bm.UserBookId = ub.UserBookId
JOIN USER u ON ub.UserId = u.UserId
JOIN BOOK b ON ub.BookId = b.BookId
WHERE u.UserName = ? AND b.Name = ?
ORDER BY bm.CreatedDate DESC
```

### QUERY 4: Books in a Collection
```sql
SELECT b.Name AS BookTitle,
       GROUP_CONCAT(DISTINCT a.AuthorName SEPARATOR ', ') AS Authors,
       b.Language, b.Format
FROM COLLECTIONBOOK cb
JOIN COLLECTION c ON cb.CollectionId = c.CollectionId
JOIN BOOK b ON cb.BookId = b.BookId
JOIN USER u ON c.UserId = u.UserId
LEFT JOIN AUTHORBOOK ab ON b.BookId = ab.BookId
LEFT JOIN AUTHOR a ON ab.AuthorId = a.AuthorId
WHERE u.UserName = ? AND c.CollectionName = ?
GROUP BY b.BookId, b.Name, b.Language, b.Format
ORDER BY b.Name
```

### QUERY 5: Average Rating for a Book
```sql
SELECT AVG(ub.UserRating) AS AverageRating,
       COUNT(ub.UserRating) AS TotalRatings
FROM USERBOOK ub
WHERE ub.BookId = ? AND ub.UserRating IS NOT NULL
```

### QUERY 6: Top Popular Books (Hot Books)
```sql
SELECT b.Name AS BookTitle,
       GROUP_CONCAT(DISTINCT a.AuthorName SEPARATOR ', ') AS Authors,
       COUNT(DISTINCT ub.UserId) AS TotalUsers,
       AVG(ub.UserRating) AS AverageRating
FROM USERBOOK ub
JOIN BOOK b ON ub.BookId = b.BookId
LEFT JOIN AUTHORBOOK ab ON b.BookId = ab.BookId
LEFT JOIN AUTHOR a ON ab.AuthorId = a.AuthorId
GROUP BY b.BookId, b.Name
ORDER BY TotalUsers DESC
LIMIT ?
```

### QUERY 7: Add New Book (Multiple INSERTs)
This is now the Book Upload flow documented above, involving:
- INSERT INTO BOOK
- INSERT INTO AUTHOR (if new)
- INSERT INTO PUBLISHER (if new)
- INSERT INTO AUTHORBOOK
- INSERT INTO GENREBOOK

---

## 🔄 Database Connection Management

All DAO classes use `ConnectionManager.getInstance().getConnection()` which:
- Uses MySQL JDBC Driver
- Implements connection pooling
- Configuration from `application.properties`:
  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/book_library
  spring.datasource.username=root
  spring.datasource.password=123456
  ```

---

## 🛡️ Security & Authentication Flow

Every protected endpoint follows this flow:

```
Frontend Request with Authorization: Bearer {JWT_token}
    ↓
JwtAuthenticationFilter.doFilterInternal()
    ↓ [Extract token from header]
JwtTokenProvider.validateToken(token)
    ↓ [Verify signature, expiration]
JwtTokenProvider.getUserIdFromJWT(token)
    ↓ [Extract userId from token claims]
UserDetailsServiceImpl.loadUserByUsername()
    ↓
UserDAO.findByUsername(username)
    → SQL: SELECT * FROM USER WHERE UserName = ?
    ↓ [Load user from database]
Spring Security sets Authentication in SecurityContext
    ↓
Controller method receives @AuthenticationPrincipal UserPrincipal
    ↓ [Contains userId, username from token]
DAO methods execute with verified userId
```

---

## 📝 Summary of SQL Operations by Table

| Table | SELECT | INSERT | UPDATE | DELETE |
|-------|--------|--------|--------|--------|
| USER | ✅ (login, find) | ✅ (register) | ❌ | ✅ (delete account) |
| BOOK | ✅ (list, search, details) | ✅ (upload) | ✅ (edit) | ✅ (delete) |
| AUTHOR | ✅ (search, list) | ✅ (find or create) | ❌ | ❌ |
| GENRE | ✅ (list, search) | ❌ | ❌ | ❌ |
| PUBLISHER | ✅ (search, list) | ✅ (find or create) | ❌ | ❌ |
| USERBOOK | ✅ (library, ratings) | ✅ (add to library) | ✅ (progress, rating) | ✅ (remove) |
| COLLECTION | ✅ (list, details) | ✅ (create) | ❌ | ✅ (delete) |
| BOOKMARK | ✅ (list) | ✅ (add) | ✅ (edit) | ✅ (delete) |
| AUTHORBOOK | ✅ (via JOINs) | ✅ (link author-book) | ❌ | ✅ (update book) |
| GENREBOOK | ✅ (via JOINs) | ✅ (link genre-book) | ❌ | ✅ (update book) |
| COLLECTIONBOOK | ✅ (via JOINs) | ✅ (add to collection) | ❌ | ✅ (remove) |

---

## 🎯 Key Design Patterns

1. **DAO Pattern**: Each table has a dedicated DAO class for database operations
2. **Service Layer**: Business logic in Service classes (AuthService, FileStorageService)
3. **DTO Pattern**: Data Transfer Objects for API requests/responses (LoginRequest, AuthResponse)
4. **Repository Pattern**: Spring's @Repository annotation for DAO classes
5. **Security Filter Chain**: JWT authentication filter for all protected endpoints
6. **Transaction Management**: Implicit via Spring's connection management
7. **Cascade Deletes**: Foreign key constraints with ON DELETE CASCADE for data integrity

---

**Document created for:** Book Library Web Application  
**Version:** 2.0.0  
**Last Updated:** November 2025
