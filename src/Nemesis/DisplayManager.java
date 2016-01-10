/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Nemesis;

import org.lwjgl.glfw.GLFWVidMode;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;



public class DisplayManager {

    public static final int WINDOW_WIDTH = 720;
    public static final int WINDOW_HEIGHT = 720;
    public static final String WINDOW_TITLE = "Nemesis";
    public static final int FPS_CAP = 120;
    public static final float WINDOW_X_MIN = -1.0f;
    public static final float WINDOW_X_MAX = 1.0f;
    public static final float WINDOW_Y_MIN = -1.0f;
    public static final float WINDOW_Y_MAX = 1.0f;
    
    private long window;
    private GLFWVidMode vidmode;
    
    DisplayManager() {
        // Initialize glfw
        if(glfwInit() == GLFW_TRUE) {
            // Set window hints
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
            
            // Get video mode
            vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            
            // Create a window
            window = createWindow(WINDOW_TITLE);
            if(window != NULL) {
                // Set position of window to center of screen
                setWindowPosCenter(vidmode);
                
                // Set key callback using Input class
                glfwSetKeyCallback(window, new Input());
                
                // Make window visible
                glfwMakeContextCurrent(window);

            } else {
                glfwTerminate();
                throw new RuntimeException("Failed to create the GLFW window");
            }
        } else {
            System.err.println("GLFW was not initialized.");
        } 
        
        // Set capabilites for current thread after window has been created
        createCapabilities();
        
        glClearColor(0.8f, 0.8f, 0.8f, 1.0f);
    }
   
    // Creates a window
    public static long createWindow(String windowTitle) {
        long window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, windowTitle, NULL, NULL);
        return window;
    }
    
    // Returns the window
    public long getWindow() {
        return this.window;
    }
    
    public final void setWindowPosCenter(GLFWVidMode vidmode) {
        glfwSetWindowPos(window, (vidmode.width()/2) - WINDOW_WIDTH/2,
                                (vidmode.height()/2) - WINDOW_HEIGHT/2);
    }
    
    public boolean shouldClose() {
        // Return wether glfw should close window or not
        return glfwWindowShouldClose(window) == GL_TRUE;
    }
    
    public void setShouldClose(boolean shouldClose) {
        // Set glfw to close window
        glfwSetWindowShouldClose(window, shouldClose ? GL_TRUE : GL_FALSE);
    }
    
    public void update() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void swapBuffers() {
        glfwSwapBuffers(window);
    }
    
    public static void cleanup() {
        glfwTerminate();
    }
}
