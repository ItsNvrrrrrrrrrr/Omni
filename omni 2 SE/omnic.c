#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// New helper function: process one DSL line and emit corresponding C code.
void process_line(const char *line, FILE *fout, char *last_method, int *inside_method) {
    // Remove leading whitespace for easier matching.
    while(*line == ' ' || *line == '\t') line++;
    
    if (strncmp(line, "get Inputer.Util;", 17) == 0) {
        fprintf(fout, "// Import Inputer.Util handled\n");
    }
    else if (strncmp(line, "public object", 13) == 0) {
        sscanf(line, "public object %s", last_method);
        fprintf(fout, "// Defining object %s\n", last_method);
    }
    else if (strncmp(line, "set void", 8) == 0) {
        sscanf(line, "set void %s", last_method);
        // Remove any trailing '{' from method name.
        char *brace = strchr(last_method, '{');
        if (brace) *brace = '\0';
        fprintf(fout, "void %s() {\n", last_method);
        *inside_method = 1;
    }
    else if (strstr(line, "new input = Inputer.Util(")) {
        fprintf(fout, "    Inputer* input = InputerUtil();\n");
    }
    else if (strstr(line, "nStr")) {
        // Expected DSL: nStr usrname = input.getInput();
        char var[50] = "", rhs[100] = "";
        sscanf(line, "nStr %s = %s", var, rhs);
        char *semi = strchr(rhs, ';'); if (semi) *semi = '\0';
        if (strstr(rhs, "input.getInput()"))
            fprintf(fout, "    char %s[100];\n    strcpy(%s, input_getInput(input));\n", var, var);
        else
            fprintf(fout, "    // Unhandled nStr assignment: %s\n", line);
    }
    else if (strncmp(line, "whileloop (true)", 16) == 0) {
        fprintf(fout, "    while(1) {\n");
    }
    else if (strstr(line, "if(") && strstr(line, ".eqls(")) {
        // Convert eqls into strcmp call.
        // Assuming format: if(usrname.eqls("root")){
        fprintf(fout, "        if(strcmp(usrname, \"root\") == 0) {\n");
    }
    else if (strstr(line, "rerun.loop;")) {
        fprintf(fout, "            continue;\n");
    }
    else if (strstr(line, "out.println")) {
        char text[200] = "";
        sscanf(line, "out.println(\"%[^\"]\")", text);
        fprintf(fout, "    printf(\"%s\\n\");\n", text);
    }
    else if (strstr(line, "}")) {
        if (*inside_method) {
            fprintf(fout, "}\n\n");
            *inside_method = 0;
        } else {
            fprintf(fout, "}\n");
        }
    }
    else {
        fprintf(fout, "// Unprocessed: %s", line);
    }
}

void compile_and_run(const char *omn_file) {
    FILE *fin = fopen(omn_file, "r");
    if (!fin) {
        printf("Error opening file: %s\n", omn_file);
        return;
    }
    FILE *fout = fopen("/tmp/temp.c", "w");
    if (!fout) {
        printf("Error creating temp file!\n");
        fclose(fin);
        return;
    }
    
    // Write standard header.
    fprintf(fout, "#include <stdio.h>\n#include <stdlib.h>\n#include <string.h>\n\n");

    char line[256];
    char method_name[50] = "";
    int inside_method = 0;
    
    while (fgets(line, sizeof(line), fin)) {
        process_line(line, fout, method_name, &inside_method);
    }
    
    // Auto-generate main if a DSL method was defined.
    fprintf(fout, "int main() {\n    %s();\n    return 0;\n}\n", method_name);
    
    fclose(fin);
    fclose(fout);
    
    system("gcc /tmp/temp.c -o /tmp/temp_exec 2>/tmp/temp.log");
    FILE *log = fopen("/tmp/temp.log", "r");
    if (log) {
        fseek(log, 0, SEEK_END);
        if (ftell(log) > 0) {
            printf("Compilation Error:\n");
            system("cat /tmp/temp.log");
            fclose(log);
            return;
        }
        fclose(log);
    }
    system("/tmp/temp_exec");
}

int main(int argc, char *argv[]) {
    if (argc != 2) {
        printf("Usage: ./omni_compiler <omn-file>\n");
        return 1;
    }

    compile_and_run(argv[1]);

    return 0;
}
