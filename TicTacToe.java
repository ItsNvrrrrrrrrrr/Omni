import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

public class TicTacToe {
    private long window;
    private int width = 800;
    private int height = 800;
    private char[][] board = new char[3][3];
    private char currentPlayer = 'X';

    public static void main(String[] args) {
        new TicTacToe().run();
    }
    public TicTacToe() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = ' ';
            }
        }
    }
    public void run() {
        init();
        loop();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void init() {
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        window = glfwCreateWindow(width, height, "Tic Tac Toe", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();
    }

    private void loop() {
        glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            drawBoard();
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void drawBoard() {
        glColor3f(0.0f, 0.0f, 0.0f);
        glLineWidth(5.0f);

        glBegin(GL_LINES);
        // Vertical lines
        glVertex2f(-0.33f, 1.0f);
        glVertex2f(-0.33f, -1.0f);
        glVertex2f(0.33f, 1.0f);
        glVertex2f(0.33f, -1.0f);

        // Horizontal lines
        glVertex2f(-1.0f, 0.33f);
        glVertex2f(1.0f, 0.33f);
        glVertex2f(-1.0f, -0.33f);
        glVertex2f(1.0f, -0.33f);
        glEnd();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == 'X') {
                    drawX(i, j);
                } else if (board[i][j] == 'O') {
                    drawO(i, j);
                }
            }
        }
    }

    private void drawX(int row, int col) {
        float x = col * 0.66f - 0.66f;
        float y = row * -0.66f + 0.66f;

        glColor3f(1.0f, 0.0f, 0.0f);
        glBegin(GL_LINES);
        glVertex2f(x - 0.2f, y - 0.2f);
        glVertex2f(x + 0.2f, y + 0.2f);
        glVertex2f(x + 0.2f, y - 0.2f);
        glVertex2f(x - 0.2f, y + 0.2f);
        glEnd();
    }

    private void drawO(int row, int col) {
        float x = col * 0.66f - 0.66f;
        float y = row * -0.66f + 0.66f;

        glColor3f(0.0f, 0.0f, 1.0f);
        glBegin(GL_LINE_LOOP);
        for (int i = 0; i < 360; i++) {
            double angle = Math.toRadians(i);
            glVertex2f(x + (float) Math.cos(angle) * 0.2f, y + (float) Math.sin(angle) * 0.2f);
        }
        glEnd();
    }
}