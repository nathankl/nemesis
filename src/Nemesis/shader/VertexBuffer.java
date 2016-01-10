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

import java.util.ArrayList;
import static org.lwjgl.opengl.GL15.*;

/**
 *
 * @author Nathan
 * @param <N> the type of values held in the GLBuffer
 */
public class VertexBuffer <N extends Number> {
    public enum VertexType {
        VERTICES,
        NORMALS,
        TEXTURE_VERTICES,
        VERTEX_COLORS,
        VERTEX_INDICES,
        NORMAL_INDICES,
        TEXTURE_INDICES,
    }
    // The OpenGL id
    private int id;
    // The buffer data
    private GLBuffer<N> buffer;
    // The values held in the buffer
    private VertexType vertexType;
    // The parent vertex array
    private VertexArray parent;
    
    private boolean isBound;
    
    public VertexBuffer(VertexArray parent, VertexType type) {
        this.parent = parent;
        this.vertexType = type;
        this.id = glGenBuffers();
        this.buffer = new GLBuffer<>(new ArrayList<>());
        this.isBound = false;
        if(buffer == null) {
            throw new RuntimeException("GLBuffer is not defined.");
        }
    }
    
    public VertexBuffer(VertexArray parent, VertexType type, GLBuffer<N> buffer) {
        // Call super constructor
        this(parent, type);
        
        if(this.parent.isBound() == false) {
            throw new IllegalStateException("Cannot bind buffer to Vertex Buffer, parent Vertex Array is not bound.");
        }
        
        this.buffer = buffer;
        
        bind(true);
        bufferData();
        bind(false);
    }
    
    public final void bind(boolean shouldBind) {
        if(this.buffer == null) {
            throw new NullPointerException("Buffer object is null, cannot bind null buffer.");
        }
        
        if(shouldBind) {
            glBindBuffer(this.buffer.getBindType(), id);
            this.isBound = true;
        } else {
            glBindBuffer(this.buffer.getBindType(), 0);
            this.isBound = false;
        }
        
    }
    
    public GLBuffer getBuffer() {
        return this.buffer;
    }
    
    /**
     * Initializes OpenGL's vertex buffer data store.
     * @throws IllegalStateException if Vertex Buffer is not bound
     */
    // Not sure if this method should be implemented in GLBuffer or here.
    private void bufferData() {
        try {
            if(this.isBound == false) {
                throw new IllegalStateException("Cannot set buffer data, Vertex Buffer is not bound.");
            }
            // Bind the buffer based on the type of values held
            switch(this.buffer.getType()) {
                case BYTE_BUFFER:
                    glBufferData(this.buffer.getBindType(), this.buffer.getBuffer(), this.buffer.getDrawType());
                    break;
                case DOUBLE_BUFFER:
                    glBufferData(this.buffer.getBindType(), this.buffer.getBuffer().asDoubleBuffer(), this.buffer.getDrawType());
                    break;
                case FLOAT_BUFFER:
                    glBufferData(this.buffer.getBindType(), this.buffer.getBuffer().asFloatBuffer(), this.buffer.getDrawType());
                    break;
                case INT_BUFFER:
                    glBufferData(this.buffer.getBindType(), this.buffer.getBuffer().asIntBuffer(), this.buffer.getDrawType());
                    break;
                case SHORT_BUFFER:
                    glBufferData(this.buffer.getBindType(), this.buffer.getBuffer().asShortBuffer(), this.buffer.getDrawType());
                    break;
                default:
                    // Should NOT execute
                    assert(false);
                    throw new RuntimeException("Buffer type is not valid.");
            }

        // Buffer type is invalid, bufferData not initialized
        } catch(RuntimeException ex) {
            System.err.println(ex.toString());
            System.err.println("Could not initialize buffer data; attempting to initialize buffer data as byte buffer...");
            glBufferData(this.buffer.getBindType(), this.buffer.getBuffer(), this.buffer.getDrawType());
        }
    }
}
