import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class OmniCompiler {
    private static Map<String, Object> variables = new HashMap<>();
    private static Stack<String> loopStack = new Stack<>();
    private static final String[] VALID_COMMANDS = {
        "public", "object", "static", "void", "main", "String", "args", "out.println", 
        "nString", "nLoop", "While", "if", "else", "break", "sys.time.sleep", "plus"
    };
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java OmniCompiler <path-to-omn-file>");
            return;
        }

        String filePath = args[0];
        String fileName = Paths.get(filePath).getFileName().toString().replace(".omn", "");
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            compileAndRun(content, fileName);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private static void compileAndRun(String content, String fileName) {
        if (content.contains("public object " + fileName)) {
            String mainMethodContent = extractMainMethodContent(content);
            if (mainMethodContent != null) {
                // Kiểm tra cú pháp
                if (!checkSyntax(mainMethodContent)) {
                    return;
                }
                executeMainMethod(mainMethodContent);
            } else {
                System.out.println("Invalid .omn file: main method not found");
            }
        } else {
            System.out.println("Error: Object name does not match file name");
        }
    }

    private static String extractMainMethodContent(String content) {
        int startIndex = content.indexOf("public static void main(String[] args){");
        if (startIndex == -1) {
            return null;
        }
        int endIndex = content.lastIndexOf("}");
        if (endIndex == -1) {
            return null;
        }
        return content.substring(startIndex + "public static void main(String[] args){".length(), endIndex).trim();
    }

    // Kiểm tra cú pháp: đảm bảo các dòng (ngoại trừ mở/đóng khối) kết thúc bằng dấu chấm phẩy
    private static boolean checkSyntax(String content) {
        String[] lines = content.split("\\n");
        boolean ok = true;
        int lineNumber = 1;
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                lineNumber++;
                continue;
            }
            if (trimmed.endsWith("{") || trimmed.endsWith("}")) {
                lineNumber++;
                continue;
            }
            if (!trimmed.endsWith(";")) {
                System.out.println("Syntax error at line " + lineNumber + ": Missing semicolon.");
                ok = false;
            }
            lineNumber++;
        }
        return ok;
    }

    private static void executeMainMethod(String mainMethodContent) {
        // Tách lệnh dựa trên dấu chấm phẩy
        String[] lines = mainMethodContent.split(";");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            if (line.startsWith("new input = Scanner.Util()")) {
                variables.put("input", scanner);
            } else if (line.startsWith("nString")) {
                String[] parts = line.split("=");
                String variableName = parts[0].split(" ")[1].trim();
                String inputCommand = parts[1].trim();
                if (inputCommand.startsWith("input.getString(")) {
                    String prompt = inputCommand.substring(inputCommand.indexOf("(") + 1, inputCommand.lastIndexOf(")")).replace("\"", "").trim();
                    System.out.print(prompt);
                    String variableValue = scanner.nextLine();
                    variables.put(variableName, variableValue);
                }
            } else if (line.startsWith("out.println")) {
                String toPrint = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")")).trim();
                if (toPrint.contains(".plus")) {
                    String[] parts = toPrint.split("\\.plus");
                    String firstPart = parts[0].trim().replace("\"", "");
                    String secondPart = parts[1].trim();
                    if (variables.containsKey(secondPart)) {
                        secondPart = (String) variables.get(secondPart);
                    }
                    System.out.println(firstPart + secondPart);
                } else {
                    if (variables.containsKey(toPrint)) {
                        System.out.println(variables.get(toPrint));
                    } else {
                        System.out.println(toPrint.replace("\"", ""));
                    }
                }
            } else if (line.matches("\\w+ = \\d+")) {
                String[] parts = line.split("=");
                String variableName = parts[0].trim();
                int variableValue = Integer.parseInt(parts[1].trim());
                variables.put(variableName, variableValue);
            } else if (line.startsWith("nLoop == While")) {
                int loopStartIndex = mainMethodContent.indexOf("{", mainMethodContent.indexOf(line)) + 1;
                int loopEndIndex = mainMethodContent.indexOf("}", loopStartIndex);
                String loopBody = mainMethodContent.substring(loopStartIndex, loopEndIndex).trim();
                loopStack.push("while");
                while (true) {
                    boolean shouldBreak = executeLoopBody(loopBody);
                    if (shouldBreak) {
                        break;
                    }
                }
                loopStack.pop();
            }
        }
    }

    private static boolean executeLoopBody(String loopBody) {
        String[] lines = loopBody.split(";");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("out.println")) {
                String toPrint = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")")).trim();
                if (variables.containsKey(toPrint)) {
                    System.out.println(variables.get(toPrint));
                } else {
                    System.out.println(toPrint.replace("\"", ""));
                }
            } else if (line.matches("\\w+\\+\\+")) {
                String variableName = line.replace("++", "").trim();
                if (variables.containsKey(variableName) && variables.get(variableName) instanceof Integer) {
                    variables.put(variableName, (Integer) variables.get(variableName) + 1);
                }
            } else if (line.startsWith("if")) {
                String condition = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")")).trim();
                if (evaluateCondition(condition)) {
                    return executeConditionalBlock(lines, line);
                }
            } else if (line.startsWith("else")) {
                return executeConditionalBlock(lines, line);
            } else if (line.equals("break")) {
                return true;
            } else if (line.startsWith("sys.time.sleep")) {
                String sleepTimeStr = line.substring(line.indexOf("(") + 1, line.lastIndexOf(")")).trim();
                int sleepTime = Integer.parseInt(sleepTimeStr);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    System.err.println("Sleep interrupted: " + e.getMessage());
                }
            }
        }
        return false;
    }

    private static boolean evaluateCondition(String condition) {
        String[] parts = condition.split("\\.eqls\\(");
        if (parts.length != 2) {
            return false;
        }
        String variableName = parts[0].trim();
        String value = parts[1].replace(")", "").replace("\"", "").trim();
        if (variables.containsKey(variableName) && variables.get(variableName) instanceof String) {
            return variables.get(variableName).equals(value);
        }
        return false;
    }

    private static boolean executeConditionalBlock(String[] lines, String currentLine) {
        int startIndex = currentLine.indexOf("{") + 1;
        int endIndex = currentLine.lastIndexOf("}");
        if (endIndex == -1) {
            endIndex = currentLine.length();
        }
        String blockContent = currentLine.substring(startIndex, endIndex).trim();
        String[] blockLines = blockContent.split(";");
        for (String blockLine : blockLines) {
            blockLine = blockLine.trim();
            if (blockLine.startsWith("out.println")) {
                String toPrint = blockLine.substring(blockLine.indexOf("(") + 1, blockLine.lastIndexOf(")")).trim();
                if (variables.containsKey(toPrint)) {
                    System.out.println(variables.get(toPrint));
                } else {
                    System.out.println(toPrint.replace("\"", ""));
                }
            }
        }
        return false;
    }
}