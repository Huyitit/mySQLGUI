# 📚 Book Library Web Application

A modern, full-stack web application for managing and reading your personal ebook library. Upload PDF/EPUB books, organize them into collections, track reading progress, bookmark pages, and read books directly in your browser with an integrated PDF reader.

## ✨ Features

### 🔐 User Authentication & Security
- **JWT-based Authentication** - Secure token-based login system
- **BCrypt Password Hashing** - Industry-standard password encryption
- **Spring Security** - Role-based access control and endpoint protection
- **User Registration** - Create new accounts with validation

### 📖 Book Management
- **Upload Books** - Support for PDF and EPUB formats (up to 50MB)
- **My Library** - Personal collection with reading status and progress tracking
- **Book Details** - View comprehensive information including authors, genres, publisher
- **Search & Discovery** - Find books by title, author, or genre
- **My Uploads** - Manage books you've uploaded
- **Edit & Delete** - Full CRUD operations for your uploaded books
- **Community Library** - Browse and discover books uploaded by other users

### 📚 Reading Experience
- **Built-in PDF Reader** - Read PDF books directly in the browser
- **Bookmarks & Highlights** - Save important pages and passages
- **Reading Progress** - Track how far you've read in each book
- **Personal Notes** - Add notes to your bookmarks

### 🗂️ Organization
- **Collections** - Create custom collections to organize books
- **Genre Browsing** - Filter books by genre (Fiction, Science, History, etc.)
- **Rating System** - Rate books on a 1-5 star scale
- **Hot Books** - See the most popular books in the community

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
