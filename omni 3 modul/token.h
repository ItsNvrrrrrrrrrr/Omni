#ifndef TOKEN_H
#define TOKEN_H

typedef enum { T_OUTP, T_PRINTLN, T_STRING, T_END } TokenType;

typedef struct {
    TokenType type;
    char value[100];
} Token;

#endif
