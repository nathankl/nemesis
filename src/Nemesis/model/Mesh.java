package Nemesis.model;

import Nemesis.GameLoop;
import Nemesis.shader.ShaderProgram;
import Nemesis.shader.VertexArray;
import Nemesis.util.BufferTools;
import java.io.File;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Mesh contains the vertex data per contiguous polygonal mesh such as vertex
 * coordinates, texture coordinates, normal coordinates, vertex colors, and face
 * indices as well as methods for rendering and updating data.
 *
 * @author Nathan
 */
public class Mesh implements GameLoop {
    // Number of meshes created.
    public static long MESH_COUNT = 0;
    
    /*
    // Number of coordinates for vertex data
    public static final int VERTEX_COORD_COUNT = 3;
    public static final int NORMAL_COORD_COUNT = 3;
    public static final int TEXTURE_COORD_COUNT = 2;
    */
    
    // Attribute list positions
    private static final int VERTICES_ATTRIB_POS = 0;
    private static final int TEXTURE_ATTRIB_POS = 1;
    private static final int NORMALS_ATTRIB_POS = 2;
    private static final int VERTEX_COLOR_ATTRIB_POS = 3;
    
    // The way VBOs should be stored in the VAO
    private static final int DEFAULT_DRAW_TYPE = GL_STATIC_DRAW;
    private static final int VBO_COUNT = 4;

    /*
    // The buffer data
    private FloatBuffer vertices;
    private FloatBuffer normals;
    private FloatBuffer textureCoord;
    private FloatBuffer vertColor;
    private IntBuffer faceVertices;
    private IntBuffer faceNormals;
    private IntBuffer faceTexture;
    */
    
    // Vertex related data
    private long vertexCount;
    private long indiceCount;
    
    // Rendering related data
    /*
    private List<Integer> VBOs;
    private int vao;
    private int ebo;
    private boolean smoothShading;
    */
    private VertexArray vao;
    
    // Number of vbos to create per mesh.
    private int vboCount;
    /*
    private boolean isVAOBound;
    */
    
    // Name of mesh, should also qualify as project-unique ID
    private String name;
    
    // The list of textures to apply to the mesh
    private List<Texture> textures;
    private Material material;
    
    // The Obj.Data object
    Obj.Data data;
    
    /**
     * Sets the mesh data and initializes other variables, then adds the data to
     * a VAO for rendering. normlIndices and textrIndices are redundant and are
     * not yet implemented.
     * @param data
     */
    public Mesh(Obj.Data data) {
        if(data == null) {
            throw new NullPointerException();
        }
        
        MESH_COUNT += 1;
        
        // Potentially unexpected behavior
        this.data = data;
        
        this.vertexCount = this.data.getVertexCount();
        this.faceCount = this.data.getFacecount();
        this.indiceCount = vertIndices.capacity();
        
        this.isVAOBound = false;
        this.vboCount = VBO_COUNT;
        
        this.vao = new VertexArray();
        
        vao.bind();
        this.VBOs.addAll(createVBOs());
        this.ebo = createEBO();
        unbindVAO();
    }
    
    /**
     * Adds a Texture to the mesh
     * @param tex the Texture object to add
     */
    public void addTexture(Texture tex) {
        // TODO: Configure what to do with multiple textures
        this.textures.add(tex);
    }

    @Override
    public void update() {
        
    }
    
    @Override
    public void render() {
        bind();
        glUniform1i(glGetUniformLocation(ShaderProgram.ACTIVE_PROGRAM, "uTexture"), 0);
        glDrawElements(GL_TRIANGLES, this.indiceCount, GL_UNSIGNED_INT, 0);
        unbind();
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Deletes the OpenGL buffer objects.
     */
    @Override
    public void cleanup() {
        // Cleanup VBOs
        for(int v : VBOs) {
            glDeleteBuffers(v);
        }
        glDeleteBuffers(ebo);
        // Cleanup VAO
        glDeleteVertexArrays(vao.getID());
        
        // Decrements MESH_COUNT field
        MESH_COUNT -= 1;
    }
    
    public String getName() {
        return this.name;
    }
    
    /**
     * @return the number of vertices
     */
    public int getVertexCount() {
        return this.vertexCount;
    }
    
    /**
     * Creates a Vertex array object and returns the id.
     * @return the id of 
     */
    private int createVAO() {
        int id = glGenVertexArrays();
        return id;
    }
    
    /**
     * Creates the vertex buffer objects and assign data to them. The vertex
     * array object must be created and bound.
     * @return a list of the buffer IDs
     */
    private List<Integer> createVBOs() {
        if(isVAOBound == false) {
            throw new IllegalStateException("VAO is not bound, cannot create VBOs.");
        }
        
        List<Integer> vboList = new ArrayList();
        IntBuffer vboIDs = BufferUtils.createIntBuffer(this.vboCount);
        glGenBuffers(vboIDs);
        
        // Assign the ids to the vboList and create the data
        for(int i = 0; i < vboIDs.capacity(); i++) {
            int v = vboIDs.get(i);
            vboList.add(i, v);
            glBindBuffer(GL_ARRAY_BUFFER, v);
            switch(i) {
                case VERTICES_ATTRIB_POS:
                    glBufferData(GL_ARRAY_BUFFER, this.vertices, GL_STATIC_DRAW);
                    break;
                case TEXTURE_ATTRIB_POS:
                    glBufferData(GL_ARRAY_BUFFER, this.textureCoord, GL_STATIC_DRAW);
                    break;
                case NORMALS_ATTRIB_POS:
                    glBufferData(GL_ARRAY_BUFFER, this.normals, GL_STATIC_DRAW);
                    break;
                case VERTEX_COLOR_ATTRIB_POS:
                    glBufferData(GL_ARRAY_BUFFER, this.vertColor, GL_STATIC_DRAW);
                    break;
                default:
                    glBufferData(GL_ARRAY_BUFFER, this.vertices, GL_STATIC_DRAW);
            }
            
            // Setup the attribute list pointer
            glVertexAttribPointer(i,
                i == TEXTURE_ATTRIB_POS ? TEXTURE_COORD_COUNT : VERTEX_COORD_COUNT,
                GL_FLOAT, false, 0, 0);
        }
        
        // Unbind buffers
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        return vboList;
    }
    
    private void addVBO(int id) {
        this.VBOs.add(id);
    }
    
    /**
     * Generates the EBO and binds the face indices to it. Does NOT add it do
     * VBO list.
     * @return EBO id
     */
    private int createEBO() {
        if(isVAOBound == false) {
            throw new IllegalStateException("VAO is not bound, cannot create EBO.");
        }
        
        int id = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, id);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, faceVertices, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        
        return id;
    }
    
    
    
    /**
     * Binds the VAO for rendering and updating.
     */
    private void bindVAO() {
        glBindVertexArray(this.vao);
        isVAOBound = true;
    }
    
    /**
     * Unbinds the VAO.
     */
    private void unbindVAO() {
        glBindVertexArray(0);
        isVAOBound = false;
    }

    private void enableVertexAttribs() {
        for(int i = 0; i < vboCount; i++) {
            glEnableVertexAttribArray(i);
        }
    }
    
    private void disableVertexAttribs() {
        for(int i = 0; i < vboCount; i++) {
            glDisableVertexAttribArray(i);
        }
    }
    
    /**
     * Binds the necessary buffer objects for rendering
     */
    private void bind() {
        // Bind textures
        textures.stream().forEach((tex) -> {
            tex.bind();
        });
        bindVAO();
        enableVertexAttribs();
        // Bind EBO
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
    }
    
    /**
     * Unbinds all bound buffer objects
     */
    private void unbind() {
        // Unbind EBO
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        disableVertexAttribs();
        unbindVAO();
        // Unbind textures
        textures.stream().forEach((tex) -> {
            tex.unbind();
        });
    }
    /**
     * Print a matrix style view of the buffers:
     * vertices, normals, textures & faceIndices
     */
    private void printBuffers() {
        // Print vertices buffer
        System.out.println("Vertices Buffer:");
        for(int i = 0; i < this.vertices.capacity(); i++) {
            System.out.print(vertices.get(i) + ", ");
            if(i % 3 == 2) {
                System.out.println();
            }
        }
        System.out.println();

        // Print normals buffer
        System.out.println("Normals Buffer:");
        for(int i = 0; i < this.normals.capacity(); i++) {
            System.out.print(normals.get(i) + ", ");
            if(i % 3 == 2) {
                System.out.println();
            }
        }
        System.out.println();

        // Print textures buffer
        System.out.println("Textures Buffer:");
        for(int i = 0; i < this.textureCoord.capacity(); i++) {
            System.out.print(textureCoord.get(i) + ", ");
            if(i % 2 == 1) {
                System.out.println();
            }
        }
        System.out.println();

        // Print face indices
        System.out.println("Indices Buffer:");
        for(int i = 0; i < this.faceVertices.capacity(); i++) {
            System.out.print(this.faceVertices.get(i) + ", ");
            if(i % 3 == 2) {
                System.out.println();
            }
        }
        System.out.println();
    }
}