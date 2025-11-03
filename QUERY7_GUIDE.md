# Query 7 Panel - Add New Book Guide

## Overview
Query 7 Panel allows users to insert their own books into the database. This feature uses **INSERT** operations on multiple tables (BOOK, AUTHOR, GENRE) and their junction tables (AUTHORBOOK, GENREBOOK).

---

## 📋 Step-by-Step Instructions

### **STEP 1: Book Information** 📖
Fill in the basic details about your book:

#### **Required Fields:**
- **Book Title*** - The name of your book
  - Cannot be empty
  - Example: "My Amazing Novel", "Learning Java", "Travel Guide 2024"

- **File Path*** - Location where the book file is stored
  - Cannot be empty
  - Example: `/books/mybook.pdf`, `/documents/novel.epub`, `C:\Users\Books\guide.pdf`

#### **Optional Fields:**
- **Language** - Language of the book (default: "English")
  - Examples: English, Vietnamese, Spanish, French, etc.

- **Format** - File format (dropdown selection)
  - Available options: PDF, EPUB, MOBI, TXT, DOCX
  - Default: PDF

- **Publisher** - Select from existing publishers (dropdown)
  - Optional: Can leave as "-- Select Publisher --"
  - Publishers are loaded from the database automatically

---

### **STEP 2: Select Authors** ✍️
Every book must have at least one author.

#### **Two Options:**

**Option A: Select Existing Authors**
1. The list shows all authors currently in the database
2. Click on an author name to select
3. Hold **Ctrl** key to select multiple authors (for co-authored books)
4. Examples:
   - Single author: Click "J.K. Rowling"
   - Multiple authors: Ctrl+Click "Stephen King", then Ctrl+Click "Peter Straub"

**Option B: Add New Author**
1. Type the new author name in the "Or add new author:" field
2. Click **"Add Author"** button
3. The new author will appear in the list
4. Select it to link with your book
5. Note: The author will be inserted into the database automatically during book submission

#### **Validation Rules:**
- ✅ At least ONE author must be selected
- ✅ Author name cannot be empty
- ✅ Duplicate author names are prevented
- ❌ Cannot submit without selecting at least one author

---

### **STEP 3: Select Genres** 📚
Link your book to one or more genre categories (optional but recommended).

#### **Two Options:**

**Option A: Select Existing Genres**
1. The list shows all genres currently in the database
2. Click on a genre name to select
3. Hold **Ctrl** key to select multiple genres
4. Examples:
   - Single genre: Click "Fiction"
   - Multiple genres: Ctrl+Click "Fantasy", then Ctrl+Click "Adventure"

**Option B: Add New Genre**
1. Type the new genre name in the "Or add new genre:" field
2. Click **"Add Genre"** button
3. The new genre will appear in the list
4. Select it to categorize your book
5. Note: The genre will be inserted into the database automatically during book submission

#### **Validation Rules:**
- ⚠️ Genres are optional (you can skip this step)
- ✅ Genre name cannot be empty
- ✅ Duplicate genre names are prevented

---

## 🚀 Submission Process

### **Submit Button: "Add Book to Database"**
When you click this button, the following happens:

#### **Phase 1: Validation** ✔️
1. Checks if Book Title is filled
2. Checks if File Path is filled
3. Checks if at least one author is selected
4. If any validation fails → Error message displayed

#### **Phase 2: Database Transaction** 🔄
The system performs a **database transaction** (all-or-nothing operation):

1. **INSERT INTO BOOK**
   ```sql
   INSERT INTO BOOK (Name, Language, FilePath, Format, UserId, PublisherId)
   VALUES (?, ?, ?, ?, ?, ?)
   ```
   - Gets generated BookId for next steps

2. **Process Authors** (for each selected author):
   - Check if author exists: `SELECT AuthorId FROM AUTHOR WHERE AuthorName = ?`
   - If NOT exists → `INSERT INTO AUTHOR (AuthorName) VALUES (?)`
   - Link to book: `INSERT INTO AUTHORBOOK (AuthorId, BookId) VALUES (?, ?)`

3. **Process Genres** (for each selected genre):
   - Check if genre exists: `SELECT GenreId FROM GENRE WHERE GenreName = ?`
   - If NOT exists → `INSERT INTO GENRE (GenreName) VALUES (?)`
   - Link to book: `INSERT INTO GENREBOOK (GenreId, BookId) VALUES (?, ?)`

#### **Phase 3: Confirmation** ✅
- **Success**: Green message "✅ Book '[Title]' successfully added to database!"
  - Form automatically clears
  - Author and Genre lists refresh
  - Ready for next book entry

- **Failure**: Red error message with details
  - Transaction is rolled back (nothing saved)
  - Form data remains for correction
  - Check error message for specific issue

---

## 🎯 Example Use Case

### **Adding "The Midnight Library" by Matt Haig**

1. **Step 1: Book Information**
   - Book Title: `The Midnight Library`
   - Language: `English`
   - File Path: `/books/fiction/midnight_library.epub`
   - Format: `EPUB`
   - Publisher: Select "Penguin Random House" from dropdown

2. **Step 2: Authors**
   - Scroll through list and find "Matt Haig"
   - Click to select
   - (If not found: Type "Matt Haig" in new author field → Click "Add Author" → Select it)

3. **Step 3: Genres**
   - Ctrl+Click "Fiction"
   - Ctrl+Click "Contemporary"
   - Ctrl+Click "Philosophical"

4. **Submit**
   - Click "Add Book to Database"
   - Wait for green success message
   - Form clears automatically

---

## 🔧 Additional Features

### **Clear Form Button**
- Resets all fields to default values
- Clears all selections
- Status message updates to "Form cleared. Ready to add new book."
- Use this when you want to start over

### **Back Button**
- Returns to Dashboard
- Your progress is NOT saved
- Make sure to submit before going back

---

## ⚠️ Important Notes

### **Transaction Safety**
- All database operations happen in a single transaction
- If ANY step fails, EVERYTHING is rolled back
- This prevents partial data (e.g., book without authors)

### **Duplicate Prevention**
- Authors: Cannot add duplicate author names to the list
- Genres: Cannot add duplicate genre names to the list
- Books: System allows duplicate book titles (different editions/formats)

### **User Association**
- Each book is automatically linked to the logged-in user
- The `UserId` field is filled automatically
- You cannot add books for other users

### **Background Processing**
- The form uses background threads to prevent UI freezing
- Large operations won't block the interface
- You'll see "Inserting book into database..." while processing

---

## 🐛 Troubleshooting

### **Error: "Book title is required"**
- Solution: Fill in the Book Title field

### **Error: "File path is required"**
- Solution: Fill in the File Path field

### **Error: "Please select at least one author"**
- Solution: Select an author from the list OR add a new author

### **Error: "Failed to add book: [SQL error]"**
- Check database connection
- Verify database.sql schema is properly loaded
- Check MySQL server is running

### **Database Transaction Failed**
- All changes are automatically rolled back
- Check the detailed error message in the dialog
- Common causes:
  - Database connection lost
  - Invalid foreign key reference
  - Duplicate constraint violation

---

## 📊 Database Schema Reference

### **Tables Modified by Query 7:**
1. **BOOK** - Main book record
2. **AUTHOR** - Author records (if new authors added)
3. **GENRE** - Genre records (if new genres added)
4. **AUTHORBOOK** - Links books to authors (many-to-many)
5. **GENREBOOK** - Links books to genres (many-to-many)

### **Fields Auto-Generated:**
- BookId (Primary Key - Auto Increment)
- AuthorId (if new author)
- GenreId (if new genre)

### **Fields Auto-Filled:**
- UserId (from logged-in user)
- Timestamp fields (if any)

---

## 💡 Tips for Best Results

1. **Use Descriptive Titles**: Make book titles clear and unique
2. **Consistent File Paths**: Use a standard format for all paths
3. **Multiple Authors**: Remember to hold Ctrl when selecting multiple authors
4. **Genre Selection**: Choose 2-3 relevant genres for better categorization
5. **Publisher Info**: Add publisher if known, helps with library organization
6. **Test with Sample Data**: Try adding a test book first to familiarize yourself

---

## 🎓 Technical Details (For Developers)

### **Key Classes:**
- `Query7Panel.java` - Main UI panel
- `BookDAO.java` - Book database operations
- `AuthorDAO.java` - Author operations
- `GenreDAO.java` - Genre operations

### **Design Patterns:**
- **SwingWorker**: Background thread execution
- **Transaction Pattern**: ACID compliance with commit/rollback
- **DAO Pattern**: Separation of data access logic

### **SQL Transaction Flow:**
```java
conn.setAutoCommit(false);
try {
    // INSERT operations
    conn.commit();
} catch (Exception e) {
    conn.rollback();
}
```

---

**Last Updated**: November 2025  
**Version**: 1.0  
**Support**: For issues, check database logs and error messages
