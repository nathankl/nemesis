/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Nemesis;

import Nemesis.model.Model;
import Nemesis.shader.ShaderProgram;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * This class is for testing new functions and debugging existing ones. Do not
 * include this class into the built project.
 * @author Nathan
 */
public class Test {
    public void run() {
        DisplayManager display = new DisplayManager();
//        ShaderProgram program = new ShaderProgram();
        //Model cubeModel = new Model("smallCube.obj");
        
        // Create a sqaure
        float[] squareVertices = new float[] {
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f
        };
        float[] squareColor = new float[] {
            0.3f, 0.7f, 0.5f, 1.0f,
            0.4f, 0.6f, 0.6f, 1.0f,
            0.5f, 0.5f, 0.7f, 1.0f,
            0.6f, 0.4f, 0.8f, 1.0f
        };
        int[] squareIndices = new int[] {
            0, 1, 3,
            3, 1, 2
        };
        ShaderProgram shaderManager = new ShaderProgram();
        shaderManager.addVertShader("simpleShader.vert");
        shaderManager.addFragShader("simpleShader.frag");
        shaderManager.link();
        
        // Setup VAO
        int vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        
        // Setup VBO
        int vboVerts = glGenBuffers();
        int vboColor = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboVerts);
        glBufferData(GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils.createFloatBuffer(squareVertices.length).put(squareVertices).flip(), GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0)
                ;
        glBindBuffer(GL_ARRAY_BUFFER, vboColor);
        glBufferData(GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils.createFloatBuffer(squareColor.length).put(squareColor).flip(), GL_STATIC_DRAW);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(1);

        // Setup EBO
        int eboID = glGenBuffers();
        IntBuffer eboBuffer = BufferUtils.createIntBuffer(squareIndices.length).put(squareIndices);
        eboBuffer.flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, eboBuffer, GL_STATIC_DRAW);
        /*
        glBindBuffer(GL_ARRAY_BUFFER, buffers.get(1));
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);
        */

       
        
        boolean running = true;
        
        while(running) {
            glfwPollEvents();
            
            display.update();
            
            shaderManager.useProgram();
            
            // Render here
//            cubeModel.render();
            //glEnableVertexAttribArray(1);
            
            glDrawElements(GL_TRIANGLES, squareIndices.length, GL_UNSIGNED_INT, 0);
//            glDrawArrays(GL_TRIANGLES, 0, squareVertices.length);
            
            //glDisableVertexAttribArray(1);

            shaderManager.unbindProgram();
            
            display.swapBuffers();
            //glfwSwapBuffers(display.getWindow());
            
            if(display.shouldClose()) {
                running = false;
            }
        }
        
        display.cleanup();
//        cubeModel.cleanup();
        
    }
    
    public void thisRuns() {
        DisplayManager thisDisplay = new DisplayManager();
        
        boolean isRunning = true;
        
        // Create a sqaure
        float[] squareVertices = new float[] {
            -0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.5f, 0.5f, 0.0f
        };
        float[] squareColor = new float[] {
            0.3f, 0.7f, 0.5f, 1.0f,
            0.4f, 0.6f, 0.6f, 1.0f,
            0.5f, 0.5f, 0.7f, 1.0f,
            0.6f, 0.4f, 0.8f, 1.0f
        };
        float[] squareIndices = new float[] {
            0, 1, 3,
            3, 1, 2
        };
        // Create shaders
        String vertShaderSrc = "#version 430 core\n"
                + "layout (location = 0) in vec3 position;\n"
                + "layout (location = 1) in vec4 color;\n"
                + "out vec4 vs_color;\n"
                + "void main(void) {\n"
                + "gl_Position = vec4(position, 1.0);\n"
                + "vs_color = color;\n"
                + "}";
        
        String fragShaderSrc = "#version 430 core\n"
                + "in vec4 vs_color;\n"
                + "out vec4 color;"
                + "void main(void) {\n"
                + "color = vs_color;\n"
                + "}";
        
        int shaderProgram = glCreateProgram();
        int vertShader = glCreateShader(GL_VERTEX_SHADER);
        int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(vertShader, vertShaderSrc);
        glShaderSource(fragShader, fragShaderSrc);
        glCompileShader(vertShader);
        glCompileShader(fragShader);
        glAttachShader(shaderProgram, vertShader);
        glAttachShader(shaderProgram, fragShader);
        glLinkProgram(shaderProgram);
        glDeleteShader(vertShader);
        glDeleteShader(fragShader);
        
        // Setup VAO
        int vaoID = glGenVertexArrays();
        IntBuffer buffers = BufferUtils.createIntBuffer(2);
        glGenBuffers(buffers);
        FloatBuffer triangleBuffer = BufferUtils.createFloatBuffer(squareVertices.length).put(squareVertices);
        FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(squareColor.length).put(squareColor);
        triangleBuffer.flip();
        colorBuffer.flip();
        glBindVertexArray(vaoID);
        glBindBuffer(GL_ARRAY_BUFFER, buffers.get(0));
        glBufferData(GL_ARRAY_BUFFER, triangleBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, buffers.get(1));
        glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        
        while(isRunning) {
            glfwPollEvents();
            
            glClearColor(0.8f, 0.8f, 0.8f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);
            
            // Draw Triangle
            glUseProgram(shaderProgram);
            
            glDrawArrays(GL_TRIANGLES, 0, 3);
            //glDrawArrays(GL_TRIANGLES, 0, triangleBuffer.capacity());
            
            glUseProgram(0);
            
            glfwSwapBuffers(thisDisplay.getWindow());
            //display.swapBuffers();
            
            if(thisDisplay.shouldClose()) {
                isRunning = false;
            }
        }
        glDeleteVertexArrays(vaoID);
        glDeleteBuffers(buffers);
        DisplayManager.cleanup();
    }
}
