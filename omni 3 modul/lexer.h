#ifndef LEXER_H
#define LEXER_H

#include <stdio.h>
#include <string.h>
#include <ctype.h>

// Token type definitions
typedef enum {
    T_OUTP, T_DOT, T_PRINTLN, T_LPAREN, T_RPAREN, T_STRING, T_UNKNOWN
} TokenType;

// Token structure
typedef struct {
    TokenType type;
    char value[256];
} Token;

// Function declarations only - NO IMPLEMENTATIONS HERE
void setSource(const char *input);
Token getNextToken(void);

#endif // LEXER_H
