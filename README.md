# ✨ FLASH-Q: Flashcard Quiz System ✨

## *I. Project Overview*

The Flashcard Quiz System is a console-based Java application designed to help users enhance their learning and self-assessment through the creation, management, and use of flashcards. The application offers a personalized experience, where each user can securely manage their flashcards and quiz results. The system connects to a relational database to store user accounts and flashcards for more efficient data management and scalability.

## *II. Key Features*

1. *🔐 User Account Management:*
    - Secure user registration and login.
    - Stores user credentials (username and password) in a MySQL database.
    - Ensures data privacy and allows each user to have their own set of flashcards.

2. *📋 Flashcard Management:*
    - Create flashcards categorized into two types:
        - *Identification Flashcards:* Open-ended questions with textual answers.
        - *True/False Flashcards:* Binary questions (True/False).
    - Add, remove, edit, and display flashcards stored in a MySQL database.
    - Manage flashcards independently for each user.

3. *🧠 Quiz Functionality:*
    - Take quizzes based on flashcards from specific categories (Identification or True/False).
    - Features to skip, reveal, and revisit skipped questions during quizzes.
    - Tracks scores and displays results after completing the quiz.

4. *📁 Database Integration:*
    - Flashcards and user accounts are stored in a MySQL database, ensuring efficient data management, persistence, and scalability.

## *III. Integration of DBMS*

The Flashcard Quiz System integrates a *Relational Database Management System (RDBMS)* for storing and managing users' data and flashcards. The application connects to a MySQL database, performing operations such as inserting, removing, editing, and retrieving flashcards.

### *Database Schema:*
1. *Users Table:*
    - Stores user data (username and password).
    - Columns: id, username, password.

2. *Identification Flashcards Table:*
    - Stores questions and answers for identification-type flashcards.
    - Columns: id, user_id, question, answer.

3. *True/False Flashcards Table:*
    - Stores questions and answers for true/false-type flashcards.
    - Columns: id, user_id, question, answer.

The program uses JDBC (Java Database Connectivity) to interact with the database, providing users with secure login, flashcard management, and quiz functionality.

## *IV. Instructions on How to Run the Program*

### *📋 Prerequisites:**
1. *Code Editor or IDE:* e.g., IntelliJ IDEA, Eclipse, or Visual Studio Code.
2. *Java Development Kit (JDK):* Ensure JDK 8 or later is installed on your system.
3. *MySQL Database:* A MySQL instance running on your machine or accessible remotely.

### *🚀 Steps to Run the Program:**

1. *📥 Download and Place Files:*
- Save all .java files into a folder called flashcard-system.

2. *🗂️ Create and Place Supporting Files:*
    - *Database Setup:*
- Run the following SQL script to create the database and tables:

```    
CREATE DATABASE DB;

USE DB;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE identification_flashcards (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE truefalse_flashcards (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    question TEXT NOT NULL,
    answer VARCHAR(5) NOT NULL, 
    FOREIGN KEY (user_id) REFERENCES users(id)
);

    - *Database Connection Configuration:*
        - In DatabaseConnection.java, ensure the correct database credentials (username, password, database URL) are set.
```
3. *🔨 Compile the Program:*
    - Open a terminal or command prompt.
    - Navigate to the flashcard-system folder.
    - Compile the program using:
 ```   
javac *.java
```
4. *▶️ Run the Program:*
    - After successful compilation, run the program by executing:
 ```   
java Main
```
### *📁 Folder Structure:*
```
flashcard-system/
├── Main.java
├── UserAccount.java
├── FlashcardManager.java
├── DatabaseConnection.java
└── (MySQL Database)
```
5. *🧩 Connector:*
- Make sure that you have the MySQL Connector JAR file in your Java project's reference libraries.
