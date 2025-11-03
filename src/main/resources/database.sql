-- Drop existing database if exists
DROP DATABASE IF EXISTS book_library;
CREATE DATABASE book_library CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE book_library;

-- =========== LEVEL 0: Independent Tables ===========

-- USER Table
CREATE TABLE USER (
    UserId INT AUTO_INCREMENT PRIMARY KEY,
    UserName VARCHAR(100) NOT NULL UNIQUE,
    HashedPassword VARCHAR(255) NOT NULL
);

-- PUBLISHER Table
CREATE TABLE PUBLISHER (
    PublisherId INT AUTO_INCREMENT PRIMARY KEY,
    PublisherName VARCHAR(255) NOT NULL
);

-- AUTHOR Table
CREATE TABLE AUTHOR (
    AuthorId INT AUTO_INCREMENT PRIMARY KEY,
    AuthorName VARCHAR(255) NOT NULL
);

-- GENRE Table
CREATE TABLE GENRE (
    GenreId INT AUTO_INCREMENT PRIMARY KEY,
    GenreName VARCHAR(100) NOT NULL UNIQUE
);

-- =========== LEVEL 1: Dependent Tables ===========

-- BOOK Table
CREATE TABLE BOOK (
    BookId INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Language VARCHAR(50),
    FilePath VARCHAR(1024) NOT NULL,
    Format VARCHAR(20),
    UserId INT,
    PublisherId INT,
    FOREIGN KEY (UserId) REFERENCES USER(UserId),
    FOREIGN KEY (PublisherId) REFERENCES PUBLISHER(PublisherId)
);

-- COLLECTION Table
CREATE TABLE COLLECTION (
    CollectionId INT AUTO_INCREMENT PRIMARY KEY,
    CollectionName VARCHAR(255) NOT NULL,
    CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    UserId INT,
    FOREIGN KEY (UserId) REFERENCES USER(UserId) ON DELETE CASCADE
);

-- =========== LEVEL 2: Advanced Dependent Tables ===========

-- USERBOOK Table (User's Library)
CREATE TABLE USERBOOK (
    UserBookId INT AUTO_INCREMENT PRIMARY KEY,
    Progress VARCHAR(50),
    AddedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    LastReadDate DATETIME,
    UserRating TINYINT CHECK (UserRating BETWEEN 1 AND 5),
    UserId INT,
    BookId INT,
    FOREIGN KEY (UserId) REFERENCES USER(UserId) ON DELETE CASCADE,
    FOREIGN KEY (BookId) REFERENCES BOOK(BookId) ON DELETE CASCADE,
    UNIQUE(UserId, BookId)
);

-- COLLECTIONBOOK Table (Many-to-Many: Collection <-> Book)
CREATE TABLE COLLECTIONBOOK (
    CollectionId INT,
    BookId INT,
    PRIMARY KEY (CollectionId, BookId),
    FOREIGN KEY (CollectionId) REFERENCES COLLECTION(CollectionId) ON DELETE CASCADE,
    FOREIGN KEY (BookId) REFERENCES BOOK(BookId) ON DELETE CASCADE
);

-- AUTHORBOOK Table (Many-to-Many: Author <-> Book)
CREATE TABLE AUTHORBOOK (
    AuthorId INT,
    BookId INT,
    PRIMARY KEY (AuthorId, BookId),
    FOREIGN KEY (AuthorId) REFERENCES AUTHOR(AuthorId) ON DELETE CASCADE,
    FOREIGN KEY (BookId) REFERENCES BOOK(BookId) ON DELETE CASCADE
);

-- GENREBOOK Table (Many-to-Many: Genre <-> Book)
CREATE TABLE GENREBOOK (
    GenreId INT,
    BookId INT,
    PRIMARY KEY (GenreId, BookId),
    FOREIGN KEY (GenreId) REFERENCES GENRE(GenreId) ON DELETE CASCADE,
    FOREIGN KEY (BookId) REFERENCES BOOK(BookId) ON DELETE CASCADE
);

-- =========== LEVEL 3: Bookmarks ===========

-- BOOKMARK Table
CREATE TABLE BOOKMARK (
    BookmarkId INT AUTO_INCREMENT PRIMARY KEY,
    BookmarkName VARCHAR(255),
    Location VARCHAR(255) NOT NULL,
    CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    UserBookId INT,
    FOREIGN KEY (UserBookId) REFERENCES USERBOOK(UserBookId) ON DELETE CASCADE
);

-- =========== Sample Data ===========

-- Insert sample users (password: "password123" hashed with BCrypt)
INSERT INTO USER (UserName, HashedPassword) VALUES
('an_nguyen', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('binh_le', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('chi_tran', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- Insert publishers
INSERT INTO PUBLISHER (PublisherName) VALUES
('Prentice Hall'),
('O''Reilly Media'),
('Addison-Wesley'),
('Penguin Random House'),
('Tor Books');

-- Insert authors
INSERT INTO AUTHOR (AuthorName) VALUES
('Robert C. Martin'),
('Martin Fowler'),
('Gang of Four'),
('Frank Herbert'),
('J.R.R. Tolkien'),
('Kathy Sierra'),
('Joshua Bloch'),
('Eric Freeman');

-- Insert genres
INSERT INTO GENRE (GenreName) VALUES
('Programming'),
('Software Engineering'),
('Science Fiction'),
('Fantasy'),
('Technical'),
('Computer Science');

-- Insert books
INSERT INTO BOOK (Name, Language, FilePath, Format, UserId, PublisherId) VALUES
('Clean Code', 'English', '/books/clean-code.pdf', 'PDF', 1, 1),
('Design Patterns', 'English', '/books/design-patterns.pdf', 'PDF', 1, 3),
('Refactoring', 'English', '/books/refactoring.pdf', 'PDF', 1, 3),
('Dune', 'English', '/books/dune.epub', 'EPUB', 2, 5),
('The Hobbit', 'English', '/books/hobbit.epub', 'EPUB', 2, 4),
('Head First Java', 'English', '/books/head-first-java.pdf', 'PDF', 1, 2),
('Effective Java', 'English', '/books/effective-java.pdf', 'PDF', 1, 3),
('Head First Design Patterns', 'English', '/books/hfdp.pdf', 'PDF', 1, 2),
('The Pragmatic Programmer', 'English', '/books/pragmatic-programmer.pdf', 'PDF', 1, 3),
('Code Complete', 'English', '/books/code-complete.pdf', 'PDF', 1, 1);

-- Link authors to books
INSERT INTO AUTHORBOOK (AuthorId, BookId) VALUES
(1, 1),  -- Robert C. Martin -> Clean Code
(3, 2),  -- Gang of Four -> Design Patterns
(2, 3),  -- Martin Fowler -> Refactoring
(4, 4),  -- Frank Herbert -> Dune
(5, 5),  -- J.R.R. Tolkien -> The Hobbit
(6, 6),  -- Kathy Sierra -> Head First Java
(7, 7),  -- Joshua Bloch -> Effective Java
(8, 8);  -- Eric Freeman -> Head First Design Patterns

-- Link genres to books
INSERT INTO GENREBOOK (GenreId, BookId) VALUES
(1, 1),  -- Clean Code -> Programming
(2, 1),  -- Clean Code -> Software Engineering
(1, 2),  -- Design Patterns -> Programming
(2, 2),  -- Design Patterns -> Software Engineering
(1, 3),  -- Refactoring -> Programming
(3, 4),  -- Dune -> Science Fiction
(4, 5),  -- The Hobbit -> Fantasy
(1, 6),  -- Head First Java -> Programming
(1, 7),  -- Effective Java -> Programming
(1, 8);  -- Head First Design Patterns -> Programming

-- Insert user library entries (USERBOOK)
INSERT INTO USERBOOK (Progress, LastReadDate, UserRating, UserId, BookId) VALUES
('65%', '2024-11-03 10:30:00', 5, 1, 1),  -- an_nguyen reading Clean Code
('100%', '2024-10-28 15:20:00', 4, 1, 2), -- an_nguyen completed Design Patterns
('40%', '2024-11-02 09:15:00', 5, 1, 3),  -- an_nguyen reading Refactoring
('0%', NULL, NULL, 1, 4),                  -- an_nguyen added Dune
('20%', '2024-10-29 14:00:00', 4, 1, 6),  -- an_nguyen reading Head First Java
('100%', '2024-10-25 18:45:00', 5, 1, 5), -- an_nguyen completed The Hobbit
('80%', '2024-11-01 11:20:00', 5, 2, 1),  -- binh_le reading Clean Code
('100%', '2024-10-30 16:30:00', 4, 2, 3), -- binh_le completed Refactoring
('50%', '2024-11-02 13:45:00', 5, 2, 7),  -- binh_le reading Effective Java
('30%', '2024-10-27 10:00:00', 4, 3, 1);  -- chi_tran reading Clean Code

-- Insert collections
INSERT INTO COLLECTION (CollectionName, UserId) VALUES
('Sách Lập trình Hay', 2),
('My Favorites', 1),
('Science Fiction Collection', 1);

-- Link books to collections
INSERT INTO COLLECTIONBOOK (CollectionId, BookId) VALUES
(1, 1),  -- Sách Lập trình Hay -> Clean Code
(1, 3),  -- Sách Lập trình Hay -> Refactoring
(1, 7),  -- Sách Lập trình Hay -> Effective Java
(2, 1),  -- My Favorites -> Clean Code
(2, 5),  -- My Favorites -> The Hobbit
(3, 4);  -- Science Fiction Collection -> Dune

-- Insert bookmarks
INSERT INTO BOOKMARK (BookmarkName, Location, UserBookId) VALUES
('Important principles', 'Page 42', 1),
('Code smells', 'Page 87', 1),
('Refactoring techniques', 'Page 125', 1),
('SOLID principles', 'Chapter 11', 7),
('Best practices', 'Page 156', 8);
