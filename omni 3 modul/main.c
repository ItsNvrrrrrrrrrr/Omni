#include <stdio.h>
#include <stdlib.h>
#include "lexer.h"
#include "parser.h"

int main(int argc, char *argv[]) {
    if (argc < 2) {
        printf("Usage: %s <file.omn>\n", argv[0]);
        return 1;
    }

    const char *filename = argv[1];
    FILE *file = fopen(filename, "r");
    if (file == NULL) {
        printf("Error: Could not open file %s\n", filename);
        return 1;
    }

    char src[1024];
    size_t index = 0;
    int c;
    while ((c = fgetc(file)) != EOF && index < sizeof(src) - 1) {
        src[index++] = c;
    }
    src[index] = '\0';
    fclose(file);

    // Set the lexer source and parse the source.
    setSource(src);
    parse();

    return 0;
}
