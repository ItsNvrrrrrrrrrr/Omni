import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Token {
    public final TokenType type;
    public final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("Token{type=%s, value='%s'}", type, value);
    }
}

enum TokenType {
    NUMBER("[0-9]+"),
    PLUS("\\+"),
    MINUS("-"),
    MULTIPLY("\\*"),
    DIVIDE("/"),
    LPAREN("\\("),
    RPAREN("\\)"),
    WHITESPACE("[ \t\f\r\n]+");

    public final String pattern;

    TokenType(String pattern) {
        this.pattern = pattern;
    }
}

class Lexer {
    private final String input;
    private final List<Token> tokens;

    public Lexer(String input) {
        this.input = input;
        this.tokens = new ArrayList<>();
    }

    public List<Token> tokenize() {
        String remainingInput = input;

        while (!remainingInput.isEmpty()) {
            boolean matched = false;

            for (TokenType tokenType : TokenType.values()) {
                Pattern pattern = Pattern.compile("^" + tokenType.pattern);
                Matcher matcher = pattern.matcher(remainingInput);

                if (matcher.find()) {
                    matched = true;
                    String tokenValue = matcher.group().trim();
                    if (tokenType != TokenType.WHITESPACE) {
                        tokens.add(new Token(tokenType, tokenValue));
                    }
                    remainingInput = matcher.replaceFirst("");
                    break;
                }
            }

            if (!matched) {
                throw new RuntimeException("Unexpected character: " + remainingInput.charAt(0));
            }
        }

        return tokens;
    }
}

public class omnic {
    public static void main(String[] args) {
        String code = "3 + 5 * (10 - 4)";
        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}