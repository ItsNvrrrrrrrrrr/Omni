#include <stdio.h>
#include <string.h>
#include "lexer.h"

void parse() {
    Token token = getNextToken();
    if (token.type == T_OUTP) {
        token = getNextToken();  // Expecting '.'
        if (token.type == T_DOT) {
            token = getNextToken();  // Expecting 'println'
            if (token.type == T_PRINTLN) {
                token = getNextToken();  // Expecting '('
                if (token.type == T_LPAREN) {
                    token = getNextToken();  // Expecting string
                    if (token.type == T_STRING) {
                        printf("%s\n", token.value);  // In chuỗi ra màn hình
                    }
                    token = getNextToken();  // Expecting ')'
                    if (token.type != T_RPAREN) {
                        printf("Error: Missing ')'\n");
                    }
                }
            }
        }
    }
}
