# 📚 Book Library Manager - Database Interacting Tool

A Java Swing desktop application demonstrating 7 core database queries (6 SELECT, 1 INSERT) for a book library management system.

## 🎯 Features

### User Authentication:
- **Login** - Secure authentication with BCrypt password hashing
- **Register** - Create new user accounts with validation

### 7 Core Queries:
1. **My Library** - View user's books with status, progress, and ratings
2. **Browse by Genre** - Find books by category
3. **My Bookmarks** - View bookmarks and notes for books
4. **My Collections** - Browse books in user collections
5. **Book Ratings** - Check average ratings for books
6. **Hot Books** - Top 5 most popular books
7. **Add New Book** - Insert your own books into the database with authors and genres

## 🛠️ Technologies

- **Java 17**
- **Maven** - Build and dependency management
- **MySQL** - Database
- **Swing** - GUI framework
- **JDBC** - Database connectivity
- **BCrypt** - Password hashing

## 📋 Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## 🚀 Setup Instructions

### 1. Database Setup

```bash
# Start MySQL server
mysql -u root -p

# Run the database script
source src/main/resources/database.sql
```

Or manually:
```bash
mysql -u root -p < src/main/resources/database.sql
```

### 2. Configure Database Connection

Edit `src/main/resources/config.properties`:

```properties
db.host=localhost
db.port=3306
db.name=book_library
db.username=root
db.password=your_mysql_password
```

### 3. Build the Application

```bash
# Clean and compile
mvn clean compile

# Package as JAR
mvn clean package
```

### 4. Run the Application

```bash
# Run with Maven
mvn exec:java -Dexec.mainClass="com.dbinteract.Main"

# Or run the JAR directly
java -jar target/BookLibraryManager.jar
```

## 👤 Sample Users

The database comes with sample data. Login credentials:

- **Username:** `an_nguyen` | **Password:** `password123`
- **Username:** `binh_le` | **Password:** `password123`
- **Username:** `chi_tran` | **Password:** `password123`

## 📁 Project Structure

```
mySQLGUI/
├── src/
│   ├── main/
│   │   ├── java/com/dbinteract/
│   │   │   ├── Main.java
│   │   │   ├── models/          # 8 entity classes
│   │   │   ├── dao/             # 8 DAO classes
│   │   │   ├── database/        # Connection manager
│   │   │   ├── ui/              # 10 Swing panels (Login, Register, Dashboard, 7 Queries)
│   │   │   └── utils/           # 3 utility classes
│   │   └── resources/
│   │       ├── config.properties
│   │       └── database.sql
│   └── test/
├── pom.xml
├── README.md
└── QUERY7_GUIDE.md           # Detailed guide for adding books
```

## 🎨 UI Screenshots

### Login Screen
Simple authentication with sample user credentials.

### Dashboard
Main menu with 6 query buttons.

### Query Panels
Each query has:
- SQL statement display
- Input parameters (where applicable)
- Execute button
- Results table/display
- Status information

**Query 7 Special Features:**
- Multi-step form for book insertion
- Real-time author/genre list loading
- Add new authors and genres on-the-fly
- Transaction-based insertion (all-or-nothing)
- See detailed instructions in `QUERY7_GUIDE.md`

## 🗄️ Database Schema

11 tables modeling a complete book library system:
- USER, BOOK, AUTHOR, GENRE, PUBLISHER
- COLLECTION, USERBOOK, BOOKMARK
- COLLECTIONBOOK, AUTHORBOOK, GENREBOOK (junction tables)

## 📊 Sample Data

Includes:
- 3 users
- 10 books
- 8 authors
- 6 genres
- 5 publishers
- 10 user-book relationships
- 3 collections
- 5 bookmarks

## 🔧 Development

### Run in Development Mode

```bash
mvn clean compile exec:java
```

### Build Executable JAR

```bash
mvn clean package
```

The JAR will be created at: `target/BookLibraryManager.jar`

## 📝 Notes

- All passwords are hashed using BCrypt
- Database connection is tested on startup
- Each query runs in a background thread (SwingWorker)
- Minimum window size: 1000x700 pixels

## 🐛 Troubleshooting

**"Database Connection Failed"**
- Ensure MySQL server is running
- Check database credentials in `config.properties`
- Verify `book_library` database exists

**"Authentication Failed"**
- Use sample credentials: `an_nguyen` / `password123`
- Check if USER table has data

**Build Errors**
- Verify Java 17+ is installed: `java -version`
- Ensure Maven is installed: `mvn -version`

## 📄 License

This is a demonstration project for educational purposes.

## 👨‍💻 Author

Created as a database interaction demonstration tool.
