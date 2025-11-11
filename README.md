# 📚 Book Library Web Application

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A modern, full-stack web application for managing and reading digital books. Built with Spring Boot, MySQL, and vanilla JavaScript, featuring JWT authentication, in-browser PDF reading, and a beautiful, responsive UI.

<img width="649" height="84" alt="image" src="https://github.com/user-attachments/assets/aab2bc8b-7ca2-4a25-8c0f-73b63ef12767" />


## ✨ Features

### 📖 Core Features

- **User Authentication & Authorization** - Secure JWT-based authentication with Spring Security
- **Book Upload & Management** - Upload PDF/EPUB files (up to 50MB) with metadata
- **In-Browser PDF Reader** - Read books directly in your browser with bookmarks and progress tracking
- **Smart Search & Filters** - Search across titles, authors, genres with real-time filtering
- **Collections & Organization** - Create custom collections to organize your library
- **Reading Progress** - Automatic bookmark saving and reading history
- **Rating & Reviews** - Rate books and see community ratings
- **User Profiles** - Personalized dashboard with reading statistics

### 🎨 Modern UI/UX

- **Responsive Design** - Works seamlessly on desktop, tablet, and mobile
- **Dark Mode Ready** - Eye-friendly color scheme with gradient accents
- **Smooth Animations** - Polished transitions and hover effects
- **Tag-based Genre Selection** - Intuitive tag interface for genre selection
- **Dropdown Menus** - Clean navigation with user dropdown for account actions

### 🔒 Security

- **JWT Token Authentication** - Stateless authentication with secure token handling
- **Password Hashing** - BCrypt encryption for password storage
- **CORS Protection** - Configured Cross-Origin Resource Sharing
- **SQL Injection Prevention** - Prepared statements for all database queries
- **Authorization Controls** - User-specific access to books and collections

## 🚀 Quick Start

### Prerequisites

- **Java 21** or higher
- **MySQL 8.0+**
- **Maven 3.9+**
- **Node.js** (optional, for development tools)

### Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/Huyitit/mySQLGUI.git
   cd mySQLGUI
   ```

2. **Set up MySQL database**

   ```bash
   mysql -u root -p < src/main/resources/database.sql
   ```

   Or manually create the database:

   ```sql
   CREATE DATABASE book_library CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **Configure application properties**

   Edit `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/book_library
   spring.datasource.username=your_username
   spring.datasource.password=your_password

   # Change JWT secret in production!
   jwt.secret=YourSecretKeyHere
   ```

4. **Build the project**

   ```bash
   mvn clean package -DskipTests
   ```

5. **Run the application**

   ```bash
   java -jar target/book-library-webapp-2.0.0.jar
   ```

6. **Access the application**

   Open your browser and navigate to: `http://localhost:8080`

## 📁 Project Structure

```
mySQLGUI/
├── src/
│   ├── main/
│   │   ├── java/com/dbinteract/
│   │   │   ├── config/              # Configuration classes
│   │   │   │   ├── FileStorageConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/          # REST API Controllers
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── BookController.java
│   │   │   │   ├── UserController.java
│   │   │   │   └── ...
│   │   │   ├── dao/                 # Data Access Objects
│   │   │   │   ├── UserDAO.java
│   │   │   │   ├── BookDAO.java
│   │   │   │   └── ...
│   │   │   ├── entity/              # Entity classes
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── security/            # Security components
│   │   │   │   ├── JwtUtil.java
│   │   │   │   └── JwtAuthenticationFilter.java
│   │   │   ├── service/             # Business logic
│   │   │   └── BookLibraryApplication.java
│   │   └── resources/
│   │       ├── static/              # Frontend files
│   │       │   ├── css/
│   │       │   │   ├── style.css
│   │       │   │   └── reader.css
│   │       │   ├── js/
│   │       │   │   ├── app.js
│   │       │   │   ├── auth.js
│   │       │   │   ├── upload.js
│   │       │   │   └── reader.js
│   │       │   ├── *.html           # HTML pages
│   │       │   └── ...
│   │       ├── application.properties
│   │       └── database.sql
│   └── test/
├── uploads/                         # Uploaded book files
├── pom.xml                          # Maven dependencies
└── README.md
```

## 🗄️ Database Schema

The application uses a relational database with the following main tables:

- **USER** - User accounts and authentication
- **BOOK** - Book metadata and file references
- **AUTHOR** - Book authors
- **PUBLISHER** - Publishing houses
- **GENRE** - Book genres/categories
- **COLLECTION** - User-created book collections
- **USER_BOOK** - User-specific book data (progress, status)
- **BOOKMARK** - Reading bookmarks
- **RATING** - User book ratings
- **BOOK_AUTHOR** - Many-to-many relationship
- **BOOK_GENRE** - Many-to-many relationship
- **COLLECTION_BOOK** - Many-to-many relationship

See `src/main/resources/database.sql` for the complete schema.

## 🔌 API Endpoints

### Authentication

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and receive JWT token

### Books

- `GET /api/books` - Get all books
- `GET /api/books/{id}` - Get book details
- `POST /api/books` - Upload new book (multipart/form-data)
- `PUT /api/books/{id}` - Update book metadata
- `DELETE /api/books/{id}` - Delete book
- `GET /api/books/search?q={query}` - Search books
- `GET /api/books/{id}/rating` - Get book ratings
- `POST /api/books/{id}/rating` - Rate a book

### User Library

- `GET /api/library` - Get user's library
- `POST /api/library` - Add book to library
- `PUT /api/library/{bookId}` - Update reading status
- `DELETE /api/library/{bookId}` - Remove from library

### Collections

- `GET /api/collections` - Get user's collections
- `POST /api/collections` - Create new collection
- `POST /api/collections/{id}/books/{bookId}` - Add book to collection
- `DELETE /api/collections/{id}` - Delete collection

### Genres, Authors, Publishers

- `GET /api/genres` - Get all genres
- `GET /api/authors/search?q={query}` - Search authors
- `GET /api/publishers/search?q={query}` - Search publishers

### User Management

- `DELETE /api/users/me` - Delete account

All protected endpoints require `Authorization: Bearer {token}` header.

## 🎨 Screenshots
### DASHBOARD

<img width="1417" height="957" alt="image" src="https://github.com/user-attachments/assets/e353361f-6963-4511-bbc2-7fe6b8ffc580" />


_Personal dashboard with reading statistics and recent activity_

### Community Library
<img width="1417" height="957" alt="image" src="https://github.com/user-attachments/assets/ec45eca2-6cfc-4901-b57e-22a3ec8c7e84" />


_Browse and discover books from the community_

### Book Reader
<img width="1417" height="957" alt="image" src="https://github.com/user-attachments/assets/db82dcd4-6583-42f0-8bb3-7cebe3d7f3c3" />


_In-browser PDF reader with bookmarks and progress tracking_

### Upload Book
<img width="1417" height="957" alt="image" src="https://github.com/user-attachments/assets/819d42d1-aefd-42ce-a5e3-36685fa7675a" />


_Upload books with intuitive tag-based genre selection_

## 🛠️ Technologies Used

### Backend

- **Spring Boot 3.2.0** - Application framework
- **Spring Security 6.2.0** - Authentication & authorization
- **Spring JDBC** - Database operations
- **MySQL 8.0** - Relational database
- **JWT (jjwt 0.12.3)** - Token-based authentication
- **Apache PDFBox 3.0.0** - PDF processing
- **Apache Commons IO** - File handling

### Frontend

- **HTML5 & CSS3** - Modern semantic markup and styling
- **JavaScript (ES6+)** - Client-side logic
- **PDF.js** - Client-side PDF rendering
- **Fetch API** - AJAX requests

### Build Tools

- **Maven 3.9+** - Dependency management
- **Spring Boot Maven Plugin** - Application packaging

## 🧪 Testing

Run tests with Maven:

```bash
mvn test
```

Run specific test class:

```bash
mvn test -Dtest=BookDAOTest
```

## 📦 Building for Production

1. Update `application.properties` with production settings
2. Change JWT secret to a strong random value
3. Build the project:
   ```bash
   mvn clean package -DskipTests
   ```
4. Deploy the JAR file:
   ```bash
   java -jar target/book-library-webapp-2.0.0.jar
   ```

### Environment Variables (Recommended for Production)

```bash
export DB_URL=jdbc:mysql://your-db-host:3306/book_library
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export JWT_SECRET=your_secure_random_secret
```

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

Please ensure your code follows the existing style and includes appropriate tests.

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Huyitit** - _Initial work_ - [GitHub](https://github.com/Huyitit)

## 🙏 Acknowledgments

- Spring Framework team for excellent documentation
- PDF.js project for client-side PDF rendering
- Bootstrap community for design inspiration
- PTIT Database Systems course for project guidance

## 📧 Contact

For questions or support, please open an issue on GitHub or contact:

- GitHub: [@Huyitit](https://github.com/Huyitit)
- Repository: [mySQLGUI](https://github.com/Huyitit/mySQLGUI)

## 🗺️ Roadmap

### Planned Features

- [ ] EPUB reader support
- [ ] Social features (following, sharing)
- [ ] Book recommendations based on reading history
- [ ] Export reading statistics
- [ ] Admin panel for content moderation
- [ ] Mobile app (React Native)
- [ ] Dark mode toggle
- [ ] Multi-language support
- [ ] Advanced search filters
- [ ] Reading goals and achievements

---

⭐ **Star this repository if you find it helpful!**

Made with ❤️ using Spring Boot and MySQL
