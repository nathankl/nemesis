package Nemesis;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWKeyCallback;

public class Input extends GLFWKeyCallback {

    public static boolean[] keys = new boolean[65536];
    
    public static int KEY_FORWARD = GLFW_KEY_W;
    public static int KEY_BACKWARD = GLFW_KEY_S;
    public static int KEY_LEFT = GLFW_KEY_A;
    public static int KEY_RIGHT = GLFW_KEY_D;
    
    @Override
    public void invoke(long window, int key, int scancode, int action, int mods) {
        // Set keys[key] to false if key is released
        keys[key] = action != GLFW_RELEASE;
    }
    
}
