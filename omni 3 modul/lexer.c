#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include "lexer.h"

// Static globals - only visible in this file
static int currentIndex = 0;
static char srcBuffer[256];

void setSource(const char *input) {
    strncpy(srcBuffer, input, sizeof(srcBuffer) - 1);
    srcBuffer[sizeof(srcBuffer) - 1] = '\0';
    currentIndex = 0;
}

Token getNextToken(void) {
    Token token = {0};  // Initialize to zero
    while (isspace(srcBuffer[currentIndex])) {
        currentIndex++;
    }

    if (strncmp(&srcBuffer[currentIndex], "outp", 4) == 0) {
        token.type = T_OUTP;
        strcpy(token.value, "outp");
        currentIndex += 4;
        return token;
    } else if (srcBuffer[currentIndex] == '.') {
        token.type = T_DOT;
        strcpy(token.value, ".");
        currentIndex++;
        return token;
    } else if (strncmp(&srcBuffer[currentIndex], "println", 7) == 0) {
        token.type = T_PRINTLN;
        strcpy(token.value, "println");
        currentIndex += 7;
        return token;
    } else if (srcBuffer[currentIndex] == '(') {
        token.type = T_LPAREN;
        strcpy(token.value, "(");
        currentIndex++;
        return token;
    } else if (srcBuffer[currentIndex] == ')') {
        token.type = T_RPAREN;
        strcpy(token.value, ")");
        currentIndex++;
        return token;
    } else if (srcBuffer[currentIndex] == '"') {
        token.type = T_STRING;
        currentIndex++;
        int i = 0;
        while (srcBuffer[currentIndex] != '"' && srcBuffer[currentIndex] != '\0') {
            token.value[i++] = srcBuffer[currentIndex++];
        }
        token.value[i] = '\0';
        currentIndex++;
        return token;
    } else {
        token.type = T_UNKNOWN;
        token.value[0] = srcBuffer[currentIndex];
        token.value[1] = '\0';
        currentIndex++;
        return token;
    }
}
