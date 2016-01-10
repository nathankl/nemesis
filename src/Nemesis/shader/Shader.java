package Nemesis.shader;

import Nemesis.util.FileTools;
import java.io.File;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL43.*;

class Shader {
    /**
     * Different types of shaders; their OpenGL value, print name and file
     * file extension.
     */
    public enum ShaderType {
        VERT_SHADER(GL_VERTEX_SHADER, "Vertex Shader", ".vert"),
        TESS_CTRL_SHADER(GL_TESS_CONTROL_SHADER, "Tesselation Control Shader", ".tessc"),
        TESS_EVAL_SHADER(GL_TESS_EVALUATION_SHADER, "Tesselation Evaluation Shader", ".tesse"),
        GEOM_SHADER(GL_GEOMETRY_SHADER, "Geometry Shader", ".geom"),
        FRAG_SHADER(GL_FRAGMENT_SHADER, "Fragment Shader", ".frag"),
        COMP_SHADER(GL_COMPUTE_SHADER, "Compute Shader", ".comp");
        
        public final int GL_TYPE;
        public final String NAME;
        public final String EXT;
        ShaderType(int glType, String name, String ext) {
            this.GL_TYPE = glType;
            this.NAME = name;
            this.EXT = ext;
        }
        
        /**
         * Gets the ShaderType given a file extension that matches one of the
         * types. User must check for null match and handle accordingly.
         * @param extension the extension of a shader file in string format
         * @return a ShaderType if a match is found, null if not
         */
        public static ShaderType getType(String extension) {
            ShaderType match = null;
            for(ShaderType type : ShaderType.values()) {
                if (type.EXT.equals(extension)) {
                    match = type;
                    break;
                }
            }
            return match;
        }
    }
    
    private static ArrayList<Integer> shaders = new ArrayList<>();
    
    private int shaderID;
    private ShaderType shaderType;
    private ShaderSource shaderSrc;
    
    /**
     * Sets type of shader based on filename, loads the source, creates and
     * compiles the OpenGL shader object.
     * @param shaderSrcFile a valid shader file
     * @throws RuntimeException
     */
    public Shader(File shaderSrcFile) {
        // Get file extension and set typePrefix name
        String ext = FileTools.getFileExtension(shaderSrcFile);
        //String shaderName;
        
        if((this.shaderType = ShaderType.getType(ext)) == null) {
            throw new RuntimeException("Could not find matching type for file extension: " + ext);
        }
        
        // Load source file into string
        String src = FileTools.readTextFile(shaderSrcFile);
        
        // Create the shader
        shaderID = glCreateShader(shaderType.GL_TYPE);
        glShaderSource(shaderID, src);
        
        // Compile and check for errors
        glCompileShader(shaderID);
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println(shaderType.NAME + " " + getID()
                    + " wasn't compiled correctly.");
        }
        
        // Add shader id to list of shaders
        shaders.add(shaderID);
        
        // Parse shader source for values.
        shaderSrc = new ShaderSource(src);
    }
    
    public final int getID() {
        return shaderID;
    }
    
    public void delete() {
        glDeleteShader(shaderID);
    }
    
    public static void cleanup() {
        shaders.stream().forEach((shader) -> {
            glDeleteShader(shader);
        });
    }
    
    public boolean hasUniform(String uniformName) {
        return shaderSrc.hasUniform(uniformName);
    }
}
