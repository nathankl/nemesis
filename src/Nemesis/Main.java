package Nemesis;

import Nemesis.model.Model;
import Nemesis.shader.ShaderProgram;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main implements Runnable, GameLoop {

    private boolean running;

    public static int WINDOW_WIDTH = 800;
    public static int WINDOW_HEIGHT = 600;
    
    private Thread thread;
    //private Model model;
    private Model cubeModel;
    private DisplayManager display;
    private ShaderProgram shaderManager;
    
    private double time;
    
    public void start() {
        running = true;
        // Get time at start
        time = glfwGetTime();
        thread = new Thread(this, "Game");
        thread.start();
    }
    
    @Override
    public void run() {
        init();
        while (running) {
            update();
            render();
            
            if(display.shouldClose()) {
                running = false;
            }
        }
        cleanup();
    }
    
    @Override
    public void init() {
        running = true;
        
        // Initialize display
        display = new DisplayManager();
        
        // Create a simple cube model
        cubeModel = new Model("smallCube.obj");
        ArrayList<String> meshNames = cubeModel.getMeshNames();
        cubeModel.getMesh(meshNames.get(0)).addTexture(0, "wall.jpg");
//        cubeModel = new Model("smallCube.obj");
        
        // Add shaders to ShaderProgram
        shaderManager = new ShaderProgram();
        shaderManager.addVertShader("simpleShader.vert");
        shaderManager.addFragShader("simpleShader.frag");
        shaderManager.link();

        // Configure how models are drawn
        //glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        //glFrontFace(GL_CCW); // GL_CCW is default
        
        
    }
    
    @Override
    public void update() {
        time = glfwGetTime();
        float stepSize = 0.025f;
        
        float stepX = 0.0f;
        float stepY = 0.0f;
        
        // Handle key presses
        glfwPollEvents();
        if(Input.keys[GLFW_KEY_ESCAPE]) {
            display.setShouldClose(true);
            
        } else if(Input.keys[Input.KEY_FORWARD]) {
            stepY = stepSize;
        } else if(Input.keys[Input.KEY_BACKWARD]) {
            stepY = -stepSize;
        } else if(Input.keys[Input.KEY_LEFT]) {
            stepX = -stepSize;
        } else if(Input.keys[Input.KEY_RIGHT]) {
            stepX = stepSize;
        }
        
        if(stepX != 0) {
            //cubeModel.moveX(stepX);
            cubeModel.update();
        }
        if(stepY != 0) {
            //cubeModel.moveY(stepY);
            cubeModel.update();
        }
        
        // Important, calls glClearBuffer
        display.update();
    }
    
    @Override
    public void render() {
        shaderManager.useProgram();
        
        /*
        float greenColor = (float) ((Math.sin(time) / 2) + 0.5);
        shaderManager.setUniform("green_color", new Float[] {greenColor});
        */
                
        //glUniform1f(vertexColorUni, greenValue);

        // Render object
        cubeModel.render();
        
        ShaderProgram.unbindProgram();
        
        // Swap buffers
        display.swapBuffers();
    }
    
    @Override
    public void cleanup() {
        cubeModel.cleanup();
        ShaderProgram.cleanup();
        DisplayManager.cleanup();
    }
    
    public static void main(String[] args) {
        new Main().start();
//        new Test().run();
    }
    
}
