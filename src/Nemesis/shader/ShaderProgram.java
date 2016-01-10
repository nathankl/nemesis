/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Nemesis.shader;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    // Directory where shaders are stored
    public static final File shaderDir = new File(System.getProperty("user.dir") + "/shaders/");
    public static int ACTIVE_PROGRAM;
    
    private static boolean isProgramInUse = false;
    private int programID;
    private ArrayList<Shader> shaders = new ArrayList<>();
    private static ArrayList<Integer> programs = new ArrayList<>();
    
    // Mandatory shaders
    private Shader vertShader;
    private Shader fragShader;
    
    public ShaderProgram() {
        // Create shaderProgram
        programID = glCreateProgram();
        programs.add(programID);
        
        // Checks shaderDir for validity
        if(shaderDir.isDirectory() == false) {
            System.err.println("File object with path: " + shaderDir.getPath()
                + "\nIs not a directory.");
        }
    }
    
    public void addVertShader(String vertSrcFileName) {
        Shader vertShader = new Shader(new File(shaderDir, vertSrcFileName));
        glAttachShader(this.programID, vertShader.getID());
        shaders.add(vertShader);
    }
    
    public void addFragShader(String fragSrcFileName) {
        Shader fragShader = new Shader(new File(shaderDir, fragSrcFileName));
        attachShader(fragShader);
        shaders.add(fragShader);
    }
    
    /**
     * Creates a shader and 
     * @param shaderSrcFileName the name of the shader source file. Must be a
     * shader file in the default shader directory.
     */
    public void addShader(String shaderSrcFileName) {
        Shader shader = new Shader(new File(shaderDir, shaderSrcFileName));
        addShader(shader);
    }
    
    /**
     * Creates and adds a shader to the program from the shaderFile. The file
     * provided must be a valid shader file.
     * @param shaderFile the file where the shader is stored.
     */
    public void addShader(File shaderFile) {
        Shader shader = new Shader(shaderFile);
        // Should check shader was created correctly before passing to addShader().
        addShader(shader);
    }
    
    /**
     * Adds the shader object to the list of shaders. Will not add shader if
     * @param shader the shader to add. Must not override another shader in the
     * program.
     */
    private void addShader(Shader shader) {
        this.shaders.add(shader);
    }
    
    /**
     * Links program with shader objects. Then deletes the GL shader objects
     * only if the program linked correctly. A vertex shader and fragment shader
     * must be defined otherwise will throw an IllegalStateException.
     * @throws IllegalStateException
     */
    public void link() {
        // Checks for mandatory shaders
        if(vertShader == null || fragShader == null) {
            throw new IllegalStateException("");
        }
        
        // Link program
        glLinkProgram(programID);
        
        // Check for program linking errors
        if(glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Shader program did not link correctly.");
        } else {
            // Delete shaders
            shaders.stream().forEach((shader) -> {
                shader.delete();
            });
        }
    }
    
    /**
     * Sets the uniform given by uniformName to value given by vector.
     * @param <T> Must be Float or Integer.
     * @param uniformName Name of the uniform to set. Must be a valid uniform
     * name of the active shader program or will throw an IllegalStateException.
     * @param vector Value to set the uniform to. Must be between the lengths of
     * 1 and 4, also must be of type java.lang.Integer[] or java.lang.Float[] or
     * will throw IllegalArgumentException
     * @throws IllegalArgumentException 
     */
    public <T extends Number> void setUniform(String uniformName, T[] vector) {
        // Unchecked, uncaught exceptions for getUniform
        int uniform = getUniform(uniformName);

        int length = vector.length;
        String type = vector.getClass().getTypeName();
        
        // Checks if the vector provided has a length between 1 and 4
        if(length >= 1 && length <= 4) {
            
            // Call glUniform*fv
            if(type.equals("java.lang.Float[]")) {
                // Create the buffer
                FloatBuffer buffer = BufferUtils.createFloatBuffer(length);
                for(T v : vector) {
                    buffer.put((Float) v);
                }
                buffer.rewind();
                
                // Set the uniform
                switch(length) {
                    case 1:
                        glUniform1fv(uniform, buffer);
                        break;
                    case 2:
                        glUniform2fv(uniform, buffer);
                        break;
                    case 3:
                        glUniform3fv(uniform, buffer);
                        break;
                    case 4:
                        glUniform4fv(uniform, buffer);
                        break;
                }
            
            // Call glUniform*iv on
            } else if(type.equals("java.lang.Integer[]")) {
                // Create the buffer
                IntBuffer buffer = BufferUtils.createIntBuffer(length);
                for(T v : vector) {
                    buffer.put((Integer) v);
                }
                buffer.rewind();
                
                // Set the uniform
                switch(length) {
                    case 1:
                        glUniform1iv(uniform, buffer);
                        break;
                    case 2:
                        glUniform2iv(uniform, buffer);
                        break;
                    case 3:
                        glUniform3iv(uniform, buffer);
                        break;
                    case 4:
                        glUniform4iv(uniform, buffer);
                        break;
                }
                
            // The vector is an invalid type, throws an exception
            } else {
                throw new IllegalArgumentException("Vector is not an Integer or Float array.");
            }
        } else {
            if(length < 1) throw new IllegalArgumentException("Vector is empty.");
            else if(length > 4) throw new IllegalArgumentException("Vector has more than 4 elements.");
        }
    }
    
    /**
     * Sets the matrix uniform given by uniformName to value given by matrix.
     * @param uniformName Name of the uniform to set. Must be a valid uniform
     * name of the active shader program or will throw an IllegalStateException.
     * @param matrix The value to set the uniform to. Must be a 2x2, 3x3, or 4x4
     * matrix or will throw an IllegalArgumentException.
     * @throws IllegalArgumentException
     */
    public void setUniform(String uniformName, float[][] matrix) {
        int uniform = getUniform(uniformName);
        // Transpose matrix from column major(matrix) to row major(arrays)
        boolean transpose = false;
        
        // Checks if matrix is empty
        if(matrix.length == 0) {
            throw new IllegalArgumentException("Matrix is empty.");
        // Checks if matrix is square
        } else if(matrix.length != matrix[0].length) {
            throw new IllegalArgumentException("Matrix is not square.");
        // Checks if matrix is the right size
        } else if(matrix.length >= 2 && matrix.length <= 4) {
            if(matrix.length < 2) throw new IllegalArgumentException("Matrix is less than 2.");
            if(matrix.length > 4) throw new IllegalArgumentException("Matrix is greater than 4.");
        } else {
            // Create buffer from matrix data
            FloatBuffer buffer = BufferUtils.createFloatBuffer(matrix.length * matrix[0].length);
            for (float[] vec : matrix) {
                for (int i = 0; i < vec.length; i++) {
                    buffer.put(vec[i]);
                }
            }
            buffer.rewind();
            
            // Set uniform value
            switch(matrix.length) {
                case 2:
                    glUniformMatrix2fv(uniform, transpose, buffer);
                    break;
                case 3:
                    glUniformMatrix3fv(uniform, transpose, buffer);
                    break;
                case 4:
                    glUniformMatrix4fv(uniform, transpose, buffer);
                    break;
            }
        }
    }
    
    public int getProgramID() {
        return programID;
    }
    
    /**
     * Attempts to retrieve the index of a GLSL uniform given uniformName from
     * the active shader program. If no shader program is active, method will 
     * throw an IllegalStateException.
     * @param uniformName the name of the uniform as written in the shader
     * program. If no program is in use, will throw an IllegalStateException.
     * @return an index to the uniform object
     * @throws IllegalArgumentException
     * @throws IllegalStateException
     */
    public int getUniform(String uniformName) {
        int uniform;
        if(isProgramInUse == false) {
            throw new IllegalStateException("There is no shader program in use. Uniform cannot be indexed.");
        }
        
        uniform = glGetUniformLocation(this.programID, uniformName);
 
        if(uniform == -1) {
            throw new IllegalArgumentException("No uniform: " + uniformName + " in program: " + this.programID);
        }
        
        return uniform;
    }

    /**
     * Enables/binds the program referred to by this. Tells this class that a
     * program is in currently in use. Will override any previous calls to 
     * useProgram with this one.
     */
    public void useProgram() {
        glUseProgram(programID);
        ACTIVE_PROGRAM = this.programID;
        // Tell the class a program is in use but don't warn if there was a
        // program already in use
        isProgramInUse = true;
    }
    
    /**
     * Unbinds the program from OpenGL by setting the active program to 0.
     */
    public static void unbindProgram() {
        glUseProgram(0);
        ACTIVE_PROGRAM = 0;
        isProgramInUse = false;
    }
    
    public static void cleanup() {
        programs.stream().forEach((program) -> {
            glDeleteProgram(program);
        });
        
        // Shouldn't actually clean anything. Shader should be deleted after it
        // was linked.
        Shader.cleanup();
    }

    /**
     * Attaches the shader to this program.
     * @param shader the shader object to add
     */
    private void attachShader(Shader shader) {
        glAttachShader(programID, shader.getID());
    }
    
}
