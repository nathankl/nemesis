/*
 * Copyright (C) 2016 Nathan
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package Nemesis.shader;

import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

/**
 *
 * @author Nathan
 */
public class VertexArray {
    // The OpenGL id for this object
    private int ID;
    private boolean isBound;
    
    private List<VertexBuffer> buffers = new ArrayList<>();
    
    private VertexArray() {
        this.isBound = false;
        this.ID = glGenVertexArrays();
    }
    
    public VertexArray(Obj objData) {
        this();
    }
    
    /**
     * Adds the buffer to the VAO buffer list.
     * @param buffer a valid buffer object
     */
    public void addBuffer(VertexBuffer buffer) {
        buffers.add(buffer);
    }
    
    public void createBuffers(List<GLBuffer> bufferList) {
        if(this.isBound == false) {
            throw new IllegalStateException("Vertex Array is not bound, cannot"
                    + "create buffers.");
        }
        if(bufferList.size() <= 0) {
            throw new IllegalArgumentException("The buffer list is zero length.");
        }
        
        // Now, assuming vertex array is bound and arguments are valid
        // Generate the vertex buffers
        IntBuffer buffers = BufferUtils.createIntBuffer(bufferList.size());
        glGenBuffers(buffers);
        
        // Create the VertexBufferObjects
        VertexBuffer vbo;
        for(GLBuffer buffer : bufferList) {
            vbo = new VertexBuffer(this, buffer);
        }
    }
    
    public void bind() {
        glBindVertexArray(ID);
        this.isBound = true;
    }
    
    public int getID() {
        return this.ID;
    }
    
    public boolean isBound() {
        
    }
}
