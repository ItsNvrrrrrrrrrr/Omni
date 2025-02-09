// This is a simple example of a new language called Onmic based on Java

public class OnmicExample {
    public static void main(String[] args) {
        System.out.println("Welcome to Onmic!");

        // Variables
        // Read content from a file with .omn extension
        try {
            java.nio.file.Path filePath = java.nio.file.Paths.get("/path/to/your/file.omn");
            String content = java.nio.file.Files.readString(filePath);
            System.out.println("File content: " + content);
        } catch (java.io.IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        int number = 10;
        String message = "The number is: ";
        System.out.println(message + number);

        // Conditional statement
        if (number > 5) {
            System.out.println("Number is greater than 5");
        } else {
            System.out.println("Number is 5 or less");
        }

        // Loop
        for (int i = 0; i < number; i++) {
            System.out.println("Loop iteration: " + i);
        }

        // Method call
        printMessage("This is a method call in Onmic");
    }

    // Method definition
    public static void printMessage(String msg) {
        System.out.println(msg);
    }
}