import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class omnicTest {

    @Test
    public void testLexerWithSimpleExpression() {
        String code = "3 + 5 * (10 - 4)";
        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();

        assertEquals(9, tokens.size());
        assertEquals(TokenType.NUMBER, tokens.get(0).type);
        assertEquals("3", tokens.get(0).value);
        assertEquals(TokenType.PLUS, tokens.get(1).type);
        assertEquals("+", tokens.get(1).value);
        assertEquals(TokenType.NUMBER, tokens.get(2).type);
        assertEquals("5", tokens.get(2).value);
        assertEquals(TokenType.MULTIPLY, tokens.get(3).type);
        assertEquals("*", tokens.get(3).value);
        assertEquals(TokenType.LPAREN, tokens.get(4).type);
        assertEquals("(", tokens.get(4).value);
        assertEquals(TokenType.NUMBER, tokens.get(5).type);
        assertEquals("10", tokens.get(5).value);
        assertEquals(TokenType.MINUS, tokens.get(6).type);
        assertEquals("-", tokens.get(6).value);
        assertEquals(TokenType.NUMBER, tokens.get(7).type);
        assertEquals("4", tokens.get(7).value);
        assertEquals(TokenType.RPAREN, tokens.get(8).type);
        assertEquals(")", tokens.get(8).value);
    }

    @Test
    public void testLexerWithWhitespace() {
        String code = "  42   +  23 ";
        Lexer lexer = new Lexer(code);
        List<Token> tokens = lexer.tokenize();

        assertEquals(3, tokens.size());
        assertEquals(TokenType.NUMBER, tokens.get(0).type);
        assertEquals("42", tokens.get(0).value);
        assertEquals(TokenType.PLUS, tokens.get(1).type);
        assertEquals("+", tokens.get(1).value);
        assertEquals(TokenType.NUMBER, tokens.get(2).type);
        assertEquals("23", tokens.get(2).value);
    }

    @Test
    public void testLexerWithInvalidCharacter() {
        String code = "3 + 5 * (10 - 4) &";
        Lexer lexer = new Lexer(code);

        Exception exception = assertThrows(RuntimeException.class, lexer::tokenize);
        assertEquals("Unexpected character: &", exception.getMessage());
    }
}