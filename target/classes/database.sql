-- =========== CÁC BẢNG KHÔNG PHỤ THUỘC (LEVEL 0) ===========
DROP DATABASE IF EXISTS book_library;
CREATE DATABASE book_library CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE book_library;
-- Quan hệ USER
CREATE TABLE USER (
    UserId INT AUTO_INCREMENT PRIMARY KEY,
    UserName VARCHAR(100) NOT NULL UNIQUE,
    HashedPassword VARCHAR(255) NOT NULL
);

-- Quan hệ PUBLISHER (Nhà xuất bản)
CREATE TABLE PUBLISHER (
    PublisherId INT AUTO_INCREMENT PRIMARY KEY,
    PublisherName VARCHAR(255) NOT NULL
);

-- Quan hệ AUTHOR (Tác giả)
CREATE TABLE AUTHOR (
    AuthorId INT AUTO_INCREMENT PRIMARY KEY,
    AuthorName VARCHAR(255) NOT NULL
);

-- Quan hệ GENRE (Thể loại)
CREATE TABLE GENRE (
    GenreId INT AUTO_INCREMENT PRIMARY KEY,
    GenreName VARCHAR(100) NOT NULL UNIQUE
);

-- =========== CÁC BẢNG PHỤ THUỘC (LEVEL 1) ===========

-- Quan hệ BOOK (Phụ thuộc vào USER và PUBLISHER)
-- Giả định UserId ở đây là người đã upload/thêm sách này vào hệ thống
CREATE TABLE BOOK (
    BookId INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Language VARCHAR(50),
    FilePath VARCHAR(1024) NOT NULL,
    Format VARCHAR(20),
    UserId INT,
    PublisherId INT,
    FOREIGN KEY (UserId) REFERENCES USER(UserId) ON DELETE CASCADE,
    FOREIGN KEY (PublisherId) REFERENCES PUBLISHER(PublisherId)
);

-- Quan hệ COLLECTION (Bộ sưu tập, phụ thuộc vào USER)
CREATE TABLE COLLECTION (
    CollectionId INT AUTO_INCREMENT PRIMARY KEY,
    CollectionName VARCHAR(255) NOT NULL,
    CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    UserId INT,
    FOREIGN KEY (UserId) REFERENCES USER(UserId) ON DELETE CASCADE
);

-- =========== CÁC BẢNG PHỤ THUỘC (LEVEL 2) ===========

-- Quan hệ USERBOOK (Liên kết User và Book, lưu tiến trình đọc)
-- Đây là bảng chi tiết cho "thư viện" của mỗi người dùng
CREATE TABLE USERBOOK (
    UserBookId INT AUTO_INCREMENT PRIMARY KEY,
    Progress VARCHAR(50),
    AddedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    LastReadDate DATETIME,
    UserRating TINYINT, -- Giả định xếp hạng từ 1-5
    UserId INT,
    BookId INT,
    FOREIGN KEY (UserId) REFERENCES USER(UserId) ON DELETE CASCADE,
    FOREIGN KEY (BookId) REFERENCES BOOK(BookId) ON DELETE CASCADE,
    UNIQUE(UserId, BookId) -- Đảm bảo mỗi user chỉ thêm 1 sách vào thư viện 1 lần
);

-- Quan hệ COLLECTIONBOOK (Bảng nối Nhiều-Nhiều giữa COLLECTION và BOOK)
CREATE TABLE COLLECTIONBOOK (
    CollectionId INT,
    BookId INT,
    PRIMARY KEY (CollectionId, BookId), -- Khóa chính tổng hợp
    FOREIGN KEY (CollectionId) REFERENCES COLLECTION(CollectionId) ON DELETE CASCADE,
    FOREIGN KEY (BookId) REFERENCES BOOK(BookId) ON DELETE CASCADE
);

-- Quan hệ AUTHORBOOK (Bảng nối Nhiều-Nhiều giữa AUTHOR và BOOK)
CREATE TABLE AUTHORBOOK (
    AuthorId INT,
    BookId INT,
    PRIMARY KEY (AuthorId, BookId), -- Khóa chính tổng hợp
    FOREIGN KEY (AuthorId) REFERENCES AUTHOR(AuthorId) ON DELETE CASCADE,
    FOREIGN KEY (BookId) REFERENCES BOOK(BookId) ON DELETE CASCADE
);

-- Quan hệ GENREBOOK (Bảng nối Nhiều-Nhiều giữa GENRE và BOOK)
CREATE TABLE GENREBOOK (
    GenreId INT,
    BookId INT,
    PRIMARY KEY (GenreId, BookId), -- Khóa chính tổng hợp
    FOREIGN KEY (GenreId) REFERENCES GENRE(GenreId) ON DELETE CASCADE,
    FOREIGN KEY (BookId) REFERENCES BOOK(BookId) ON DELETE CASCADE
);

-- =========== CÁC BẢNG PHỤ THUỘC (LEVEL 3) ===========

-- Quan hệ BOOKMARK (Đánh dấu trang, phụ thuộc vào USERBOOK)
CREATE TABLE BOOKMARK (
    BookmarkId INT AUTO_INCREMENT PRIMARY KEY,
    BookmarkName VARCHAR(255),
    Location VARCHAR(255) NOT NULL, -- Có thể là số trang, hoặc một chuỗi vị trí
    CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    UserBookId INT,
    -- Khi một sách bị xóa khỏi thư viện (xóa UserBook), các bookmark cũng bị xóa
    FOREIGN KEY (UserBookId) REFERENCES USERBOOK(UserBookId) ON DELETE CASCADE
);

SELECT * FROM user;
SELECT * FROM publisher;
SELECT * FROM author;
SELECT * FROM genre;
SELECT * FROM book;
SELECT * FROM collection;
SELECT * FROM userbook;
SELECT * FROM collectionbook;
SELECT * FROM authorbook;
SELECT * FROM genrebook;
SELECT * FROM bookmark;

INSERT INTO
    GENRE (GenreName)
VALUES ('Programming'),
    ('Software Engineering'),
    ('Computer Science'),
    ('Technical'),
    ('Computers / Technology'),
    ('Science'),
    ('Science Fiction'),
    ('Fantasy'),
    ('Mystery'),
    ('Suspense / Thriller'),
    ('Horror'),
    ('Romance'),
    ('Historical Fiction'),
    ('Literary Fiction'),
    ('Contemporary'),
    ('Dystopian'),
    ('Post-Apocalyptic'),
    ('Adventure'),
    ('Classic'),
    ('Crime'),
    ('Humor / Comedy'),
    ('Satire'),
    ('Poetry'),
    ('Short Story'),
    ('Anthology'),
    ('Teen / Young Adult (YA)'),
    ('Children\'s'),
    ('Comic Book / Graphic Novel'),
    ('Manga'),
    ('Art / Photography'),
    ('Autobiography / Memoir'),
    ('Biography'),
    ('Business / Economics'),
    ('Cooking / Food'),
    ('Crafts / Hobbies'),
    ('Education / Reference'),
    ('Health / Fitness'),
    ('History'),
    ('Home / Garden'),
    ('Journalism'),
    ('Law / Criminology'),
    ('Philosophy'),
    ('Politics / Government'),
    ('Psychology'),
    ('Religion / Spirituality'),
    (
        'Self-Help / Personal Development'
    ),
    ('Social Science'),
    ('Sports'),
    ('Travel'),
    ('True Crime'),
    ('Mythology / Folklore'),
    ('Western'),
    ('Alternate History'),
    ('Steampunk'),
    ('Cyberpunk'),
    ('Space Opera'),
    ('Urban Fantasy'),
    ('High Fantasy'),
    ('Epic Fantasy'),
    ('Dark Fantasy'),
    ('Magical Realism');
