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

INSERT INTO identification_flashcards (user_id, question, answer) VALUES
(1, 'What is the capital city of France?', 'Paris'),
(1, 'Who wrote Romeo and Juliet?', 'William Shakespeare'),
(1, 'What is the chemical symbol for gold?', 'Au'),
(1, 'What is the largest planet in the Solar System?', 'Jupiter'),
(1, 'Who painted the Mona Lisa?', 'Leonardo da Vinci');

INSERT INTO truefalse_flashcards (user_id, question, answer) VALUES
(1, 'The Earth is flat.', 'False'),
(1, 'Water boils at 100 degrees Celsius at sea level.', 'True'),
(1, 'The Great Wall of China is visible from space.', 'False'),
(1, 'Lightning never strikes the same place twice.', 'False'),
(1, 'Humans share about 60% of their DNA with bananas.', 'True');

INSERT INTO users (username, password) VALUES ('jaycee', 'andal');