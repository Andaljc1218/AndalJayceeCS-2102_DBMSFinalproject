import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FlashcardManager {
    private final int userId;

    public FlashcardManager(int userId) {
        this.userId = userId;
    }

    public void addFlashcard(Scanner scanner) {
        System.out.println();
        System.out.println("Choose flashcard type to add:");
        System.out.println("1. Identification");
        System.out.println("2. True/False");
        System.out.print("Enter your choice (leave blank to cancel): ");
        String choice = scanner.nextLine();

        if (choice.isBlank()) {
            System.out.println("Operation canceled.");
            return;
        }

        int typeChoice;
        try {
            typeChoice = Integer.parseInt(choice);
            if (typeChoice < 1 || typeChoice > 2) {
                System.out.println("Invalid choice. Please select 1 or 2.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (typeChoice == 1) {
                addIdentificationFlashcard(scanner, conn);
            } else if (typeChoice == 2) {
                addTrueFalseFlashcard(scanner, conn);
            }
        } catch (SQLException e) {
            System.out.println("Error while adding flashcard: " + e.getMessage());
        }
    }

    private void addIdentificationFlashcard(Scanner scanner, Connection conn) throws SQLException {
        while (true) {
            System.out.println();
            System.out.print("Enter the question (leave blank to cancel): ");
            String question = scanner.nextLine();
            if (question.isBlank()) {
                System.out.println("Operation canceled.");
                return;
            }

            System.out.print("Enter the answer: ");
            String answer = scanner.nextLine();

            String sql = "INSERT INTO identification_flashcards (user_id, question, answer) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setString(2, question);
                stmt.setString(3, answer);
                stmt.executeUpdate();
                System.out.println("Flashcard added successfully.");
            }

            System.out.print("Do you want to add another flashcard? (yes/no): ");
            String another = scanner.nextLine();
            if (!another.equalsIgnoreCase("yes")) {
                break;
            }
        }
    }

    private void addTrueFalseFlashcard(Scanner scanner, Connection conn) throws SQLException {
        while (true) {
            System.out.println();
            System.out.print("Enter the question (leave blank to cancel): ");
            String question = scanner.nextLine();
            if (question.isBlank()) {
                System.out.println("Operation canceled.");
                return;
            }

            System.out.print("Enter the answer (True/False): ");
            String answer = scanner.nextLine();

            String sql = "INSERT INTO truefalse_flashcards (user_id, question, answer) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);        
                stmt.setString(2, question);  
                stmt.setString(3, answer);    
                stmt.executeUpdate();
                System.out.println("Flashcard added successfully.");
            } catch (SQLException e) {
                System.out.println("Error while adding flashcard: " + e.getMessage());
            }

            System.out.print("Do you want to add another flashcard? (yes/no): ");
            String another = scanner.nextLine();
            if (!another.equalsIgnoreCase("yes")) {
                break;
            }
        }
    }

    public void removeFlashcard(Scanner scanner) {
        System.out.println();
        System.out.println("Choose flashcard type to remove:");
        System.out.println("1. Identification");
        System.out.println("2. True/False");
        System.out.println("3. Remove All (Identification)");
        System.out.println("4. Remove All (True/False)");
        System.out.println("5. Remove All Flashcards");
        System.out.print("Enter your choice (leave blank to cancel): ");
        String choice = scanner.nextLine();
    
        if (choice.isBlank()) {
            System.out.println("Operation canceled.");
            return;
        }
    
        int typeChoice;
        try {
            typeChoice = Integer.parseInt(choice);
            if (typeChoice < 1 || typeChoice > 5) {
                System.out.println("Invalid choice. Please select a number between 1 and 5.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return;
        }
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            switch (typeChoice) {
                case 1 -> removeFlashcardFromTable(scanner, conn, "identification_flashcards");
                case 2 -> removeFlashcardFromTable(scanner, conn, "truefalse_flashcards");
                case 3 -> removeAllFromTable(conn, "identification_flashcards");
                case 4 -> removeAllFromTable(conn, "truefalse_flashcards");
                case 5 -> {
                    removeAllFromTable(conn, "identification_flashcards");
                    removeAllFromTable(conn, "truefalse_flashcards");
                }
                default -> System.out.println("Invalid choice. Operation canceled.");
            }
        } catch (SQLException e) {
            System.out.println("Error while removing flashcard(s): " + e.getMessage());
        }
    }
    
    private void removeFlashcardFromTable(Scanner scanner, Connection conn, String tableName) throws SQLException {
        String sql = "SELECT id, question, answer FROM " + tableName + " WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
    
            List<Integer> idList = displayFlashcardsWithIndex(rs);
    
            if (idList.isEmpty()) {
                System.out.println("No flashcards available to remove.");
                return;
            }
    
            System.out.print("Enter the index of the flashcard to remove (leave blank to cancel): ");
            String indexInput = scanner.nextLine();
            if (indexInput.isBlank()) {
                System.out.println("Operation canceled.");
                return;
            }
    
            try {
                int index = Integer.parseInt(indexInput);
                if (index < 1 || index > idList.size()) {
                    System.out.println("Invalid index. Please select a valid index.");
                    return;
                }
    
                int flashcardId = idList.get(index - 1);
                String deleteSql = "DELETE FROM " + tableName + " WHERE id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, flashcardId);
                    deleteStmt.executeUpdate();
                    System.out.println("Flashcard removed successfully.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid index.");
            }
        }
    }
    
    private void removeAllFromTable(Connection conn, String tableName) throws SQLException {
        String deleteAllSql = "DELETE FROM " + tableName + " WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(deleteAllSql)) {
            stmt.setInt(1, userId);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("All flashcards from " + tableName + " removed successfully.");
            } else {
                System.out.println("No flashcards found to remove.");
            }
        }
    }

    public void editFlashcard(Scanner scanner) {
        System.out.println();
        System.out.println("Choose flashcard type to edit:");
        System.out.println("1. Identification");
        System.out.println("2. True/False");
        System.out.print("Enter your choice (leave blank to cancel): ");
        String choice = scanner.nextLine();
    
        if (choice.isBlank()) {
            System.out.println("Operation canceled.");
            return;
        }
    
        String tableName;
        if (choice.equals("1")) {
            tableName = "identification_flashcards";
        } else if (choice.equals("2")) {
            tableName = "truefalse_flashcards";
        } else {
            System.out.println("Invalid choice. Operation canceled.");
            return;
        }
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            String selectSql = "SELECT id, question, answer FROM " + tableName + " WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
    
                List<Integer> idList = new ArrayList<>();
                System.out.println("Flashcards:");
                int index = 1;
                while (rs.next()) {
                    System.out.println(index + ". Question: " + rs.getString("question"));
                    System.out.println("   Answer: " + rs.getString("answer"));
                    idList.add(rs.getInt("id"));
                    index++;
                }
    
                if (idList.isEmpty()) {
                    System.out.println("No flashcards available.");
                    return;
                }
    
                System.out.print("\nEnter the index of the flashcard to edit (leave blank to cancel): ");
                String indexInput = scanner.nextLine();
                if (indexInput.isBlank()) {
                    System.out.println("Operation canceled.");
                    return;
                }
    
                int selectedIndex;
                try {
                    selectedIndex = Integer.parseInt(indexInput);
                    if (selectedIndex < 1 || selectedIndex > idList.size()) {
                        System.out.println("Invalid index. Operation canceled.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Operation canceled.");
                    return;
                }
    
                int flashcardId = idList.get(selectedIndex - 1);
    
                System.out.print("Enter the new question (leave blank to keep unchanged): ");
                String newQuestion = scanner.nextLine();
                System.out.print("Enter the new answer (leave blank to keep unchanged): ");
                String newAnswer = scanner.nextLine();
    
                String updateSql = "UPDATE " + tableName + " SET question = COALESCE(NULLIF(?, ''), question), answer = COALESCE(NULLIF(?, ''), answer) WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, newQuestion);
                    updateStmt.setString(2, newAnswer);
                    updateStmt.setInt(3, flashcardId);
                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Flashcard updated successfully!");
                    } else {
                        System.out.println("Error updating flashcard.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while editing flashcard: " + e.getMessage());
        }
    }

    public void displayFlashcards() {
        System.out.println();
        System.out.println("Displaying all flashcards:");

        try (Connection conn = DatabaseConnection.getConnection()) {
            displayFlashcardsByType(conn, "identification_flashcards", "Identification");
            displayFlashcardsByType(conn, "truefalse_flashcards", "True/False");
        } catch (SQLException e) {
            System.out.println("Error while displaying flashcards: " + e.getMessage());
        }
    }

    private void displayFlashcardsByType(Connection conn, String tableName, String typeName) throws SQLException {
        String sql = "SELECT question, answer FROM " + tableName + " WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n" + typeName + " Flashcards:");
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println(count + ". Question: " + rs.getString("question"));
                System.out.println("   Answer: " + rs.getString("answer"));
            }

            if (count == 0) {
                System.out.println("No flashcards available.");
            }
        }
    }

    public void takeQuiz(Scanner scanner) {
        System.out.println();
        System.out.println("Choose quiz type:");
        System.out.println("1. Identification");
        System.out.println("2. True/False");
        System.out.println("3. Randomized");
        System.out.print("Enter your choice (leave blank to cancel): ");
        String choice = scanner.nextLine();

        if (choice.isBlank()) {
            System.out.println("Operation canceled.");
            return;
        }

        int typeChoice;
        try {
            typeChoice = Integer.parseInt(choice);
            if (typeChoice < 1 || typeChoice > 3) {
                System.out.println("Invalid choice. Please select between 1 and 3.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (typeChoice == 1) {
                runQuiz(conn, "identification_flashcards", "Identification", scanner);
            } else if (typeChoice == 2) {
                runQuiz(conn, "truefalse_flashcards", "True/False", scanner);
            } else if (typeChoice == 3) {
                runQuiz(conn, "identification_flashcards", "Identification", scanner);
                runQuiz(conn, "truefalse_flashcards", "True/False", scanner);
            }
        } catch (SQLException e) {
            System.out.println("Error while taking the quiz: " + e.getMessage());
        }
    }

    private void runQuiz(Connection conn, String tableName, String typeName, Scanner scanner) throws SQLException {
        String sql = "SELECT id, question, answer FROM " + tableName + " WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            List<Integer> skippedQuestions = new ArrayList<>();
            List<Integer> questionIds = new ArrayList<>();
            List<String> questions = new ArrayList<>();
            List<String> answers = new ArrayList<>();

            while (rs.next()) {
                questionIds.add(rs.getInt("id"));
                questions.add(rs.getString("question"));
                answers.add(rs.getString("answer"));
            }

            if (questions.isEmpty()) {
                System.out.println("No flashcards available for the " + typeName + " quiz.");
                return;
            }

            System.out.println("\nStarting " + typeName + " Quiz:");
            int score = 0;

            for (int i = 0; i < questions.size(); i++) {
                System.out.println("\nQuestion " + (i + 1) + ": " + questions.get(i));
                System.out.println("Options: [1] Answer [2] Skip [3] Reveal");
                System.out.print("Your choice: ");
                String action = scanner.nextLine();

                switch (action) {
                    case "1":
                        System.out.print("Your answer: ");
                        String userAnswer = scanner.nextLine();
                        if (userAnswer.equalsIgnoreCase(answers.get(i))) {
                            System.out.println("Correct!");
                            score++;
                        } else {
                            System.out.println("Wrong. Correct answer: " + answers.get(i));
                        }
                        break;
                    case "2":
                        skippedQuestions.add(i);
                        break;
                    case "3":
                        System.out.println("Answer revealed: " + answers.get(i));
                        break;
                    default:
                        System.out.println("Invalid choice. Moving to the next question.");
                }
            }

            if (!skippedQuestions.isEmpty()) {
                System.out.println("\nAnswering skipped questions...");
                for (int i : skippedQuestions) {
                    System.out.println("\nQuestion (Skipped): " + questions.get(i));
                    System.out.print("Your answer: ");
                    String userAnswer = scanner.nextLine();
                    if (userAnswer.equalsIgnoreCase(answers.get(i))) {
                        System.out.println("Correct!");
                        score++;
                    } else {
                        System.out.println("Wrong. Correct answer: " + answers.get(i));
                    }
                }
            }

            System.out.println("\n" + typeName + " Quiz finished!");
            System.out.println("Your score: " + score + "/" + questions.size());
        }
    }

    private List<Integer> displayFlashcardsWithIndex(ResultSet rs) throws SQLException {
        List<Integer> idList = new ArrayList<>();
        int index = 1;
        while (rs.next()) {
            System.out.println(index + ". Question: " + rs.getString("question"));
            idList.add(rs.getInt("id"));
            index++;
        }
        return idList;
    }
}