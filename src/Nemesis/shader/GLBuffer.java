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
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

/**
 *
 * @author Nathan
 * @param <N> what values the Buffer holds
 */
public class GLBuffer <N extends Number> {
    
    public enum BufferType {
        BYTE_BUFFER(    ByteBuffer.class,      Byte.class ),
        DOUBLE_BUFFER(DoubleBuffer.class,    Double.class ),
        FLOAT_BUFFER(  FloatBuffer.class,     Float.class ),
        INT_BUFFER(      IntBuffer.class,   Integer.class ),
        SHORT_BUFFER(  ShortBuffer.class,     Short.class );
        // Unsupported buffers
        //CHAR_BUFFER(    CharBuffer.class, Character.class ),
        //LONG_BUFFER(    LongBuffer.class,      Long.class ),
        
        private Class BUFFER_CLASS;
        private Class VALUE_CLASS;
        BufferType(Class bufferClass, Class valueClass) {
            this.BUFFER_CLASS = bufferClass;
            this.VALUE_CLASS = valueClass;
        }
        
        /**
         * Gets the number of bytes to contain the value getType.
         * @return number of bytes for a value, or 0 if not match is found
         */
        public int getByteSize() {
            int byteSize = 0;
            switch(VALUE_CLASS.getSimpleName()) {
                case "Byte":
                    byteSize = Byte.BYTES;
                    break;
                case "Character":
                    byteSize = Character.BYTES;
                    break;
                case "Double":
                    byteSize = Double.BYTES;
                    break;
                case "Float":
                    byteSize = Float.BYTES;
                    break;
                case "Integer":
                    byteSize = Integer.BYTES;
                    break;
                case "Long":
                    byteSize = Long.BYTES;
                    break;
                case "Short":
                    byteSize = Short.BYTES;
                    break;
            }
            if(byteSize == 0) {
                throw new RuntimeException("Unable to get number of bytes.");
            }
            return byteSize;
        }
        
        /**
         * Takes an instance of Buffer and checks the class name to see if it
         * contains the class name of a Buffer base class. For example:
         * InstanceName.contains(SimpleName); // where ->
         * InstanceName = "java.nio.DirectFloatBufferS";
         * SimpleName = "FloatBuffer"; // Enumeration value class name
         * 
         * @param <B> the getType of buffer as input
         * @param buffer the buffer to check getType
         * @return BufferType if match found, null if not
         */
        public static <B extends Buffer> BufferType getType(B buffer) {
            if(buffer == null) {
                throw new NullPointerException("Buffer is null.");
            }
            BufferType type = null;
            String instanceClassName = buffer.getClass().getName();
            
            // Check if buffer class name contains the simple enum name
            for(BufferType t : BufferType.values()) {
                if(instanceClassName.contains(t.BUFFER_CLASS.getSimpleName())) {
                    type = t;
                    break;
                }
            }
            return type;
        }
        
        /**
         * Gets BufferType given a Number. User should check for null return.
         * @param <N> the getType of value, a Number
         * @param value the Number to match against enum values
         * @return BufferType if match is found, null if not
         * @throws NullPointerException
         */
        public static <N extends Number> BufferType getType(N value) {
            if(value == null) {
                throw new NullPointerException();
            }
            BufferType type = null;
            String instanceClassName = value.getClass().getName();
            
            for(BufferType t : BufferType.values()) {
                if(instanceClassName.contains(t.VALUE_CLASS.getSimpleName())) {
                    type = t;
                    break;
                }
            }
            
            if(type == null) {
                throw new NullPointerException();
            }
            
            return type;
        }
    }
    
    public enum GLBindType {
        ARRAY_BUFFER(GL_ARRAY_BUFFER),
        ELEMENT_ARRAY_BUFFER(GL_ELEMENT_ARRAY_BUFFER);
        /* Unimplemented buffer types
        PIXEL_PACK_BUFFER(GL_PIXEL_PACK_BUFFER),
        PIXEL_UNPACK_BUFFER(GL_PIXEL_UNPACK_BUFFER),
        TRANSFORM_FEEDBACK_BUFFER(GL_TRANSFORM_FEEDBACK_BUFFER),
        UNIFORM_BUFFER(GL_UNIFORM_BUFFER),
        TEXTURE_BUFFER(GL_TEXTURE_BUFFER),
        COPY_READ_BUFFER(GL_COPY_READ_BUFFER),
        COPY_WRITE_BUFFER(GL_COPY_WRITE_BUFFER),
        DRAW_INDIRECT_BUFFER(GL_DRAW_INDIRECT_BUFFER),
        ATOMIC_COUNTER_BUFFER(GL_ATOMIC_COUNTER_BUFFER),
        DISPATCH_INDIRECT_BUFFER(GL_DISPATCH_INDIRECT_BUFFER),
        SHADER_STORAGE_BUFFER(GL_SHADER_STORAGE_BUFFER),
        PARAMETER_BUFFER_ARB(GL_PARAMETER_BUFFER_ARB);
        */
        
        public final int GL_BIND;
        GLBindType(int glType) {
            this.GL_BIND = glType;
        }
        
        public static GLBindType getType(int glBufferType) {
            GLBindType type = null;
            for(GLBindType t : GLBindType.values()) {
                if(t.GL_BIND == glBufferType) {
                    type = t;
                    break;
                }
            }
            return type;
        }
    }
    
    public enum GLDrawType {
        STREAM_DRAW(GL_STREAM_DRAW),
        STREAM_READ(GL_STREAM_READ),
        STREAM_COPY(GL_STREAM_COPY),
        STATIC_DRAW(GL_STATIC_DRAW),
        STATIC_READ(GL_STATIC_READ),
        STATIC_COPY(GL_STATIC_COPY),
        DYNAMIC_DRAW(GL_DYNAMIC_DRAW),
        DYNAMIC_READ(GL_DYNAMIC_READ),
        DYNAMIC_COPY(GL_DYNAMIC_COPY);
        
        public int GL_DRAW;
        GLDrawType(int glDrawType) {
            this.GL_DRAW = glDrawType;
        }
        
        public static GLDrawType getType(int glDrawType) {
            GLDrawType type = null;
            for(GLDrawType t : GLDrawType.values()) {
                if(t.GL_DRAW == glDrawType) {
                    type = t;
                    break;
                }
            }
            return type;
        }
    }

    private final GLDrawType DEFAULT_DRAW = GLDrawType.STATIC_DRAW;
    private final GLBindType DEFAULT_BIND = GLBindType.ARRAY_BUFFER;
    
    // The wrapped buffer
    private ByteBuffer buffer;
    private BufferType type;
    
    // Buffer related fields
    private int byteSize;
    private int bufferSize;
    
    // Type of gl buffer
    private GLDrawType glDrawType;
    private GLBindType glBindType;
    
    /**
     * Assigns the data to the buffer. This is the bare minimum constructor.
     * Calls super constructor for setting all values that weren't defined here.
     * @param data the data to store into the buffer
     */
    public GLBuffer(ArrayList<N> data) {
        this.glBindType = DEFAULT_BIND;
        this.glDrawType = DEFAULT_DRAW;
        
        // Setup the buffer data
        try {
            if(data == null) {
                throw new NullPointerException();
            } else if(data.size() < 1) {
                throw new IllegalArgumentException("Data list is zero length.");
            } 
            this.type = BufferType.getType(data.get(0));
            this.byteSize = type.getByteSize();
            
        
        // Default to empty buffer
        } catch(IllegalArgumentException | NullPointerException ex) {
            System.err.println(ex.toString() + "Creating empty default buffer...");
            // Create empty buffer
            data = new ArrayList<>();
            
            // Set getType of buffer by checking the instantiated variable
            N val = (N) (Number) 1;
            this.type = BufferType.getType(val);
            
            // Get byte size from getType
            this.byteSize = type.getByteSize();
            
        // Create the buffer with the setup data
        // Probably should NOT put this code into finally but it's so convenient
        } finally {
            this.buffer = ByteBuffer.allocateDirect(data.size() * this.byteSize);
            for(Number n : data) {
                switch(this.type) {
                    case BYTE_BUFFER:
                        this.buffer.put((Byte) n);
                        break;
                    case DOUBLE_BUFFER:
                        this.buffer.putDouble((Double) n);
                        break;
                    case FLOAT_BUFFER:
                        this.buffer.putFloat((Float) n);
                        break;
                    case INT_BUFFER:
                        this.buffer.putInt((Integer) n);
                        break;
                    case SHORT_BUFFER:
                        this.buffer.putShort((Short) n);
                        break;
                    default:
                        // Should NOT execute. If it does, something's broke.
                        //System.err.println("Something's broke.");;
                        System.err.println(this.type.toString() + " does not match any predefined type.");
                }
            }
            this.buffer.flip();
            this.bufferSize = this.buffer.capacity() / this.byteSize;
            if(this.bufferSize != data.size()){
                System.err.println("Data was not transferred correctly to buffer.");
            }
        }
    }
    
    /**
     * 
     * @param data the data to store into the buffer
     * @param glBindType where OpenGL binds the buffer
     * @param glDrawType the way OpenGL draws the buffer
     */
    public GLBuffer(ArrayList<N> data, int glBindType, int glDrawType) {
        // First call super constructor
        this(data);
        
        String bindErr = "BIND";
        String drawErr = "DRAW";
        try {
            // Set fields
            this.glDrawType = GLDrawType.getType(glDrawType);
            this.glBindType = GLBindType.getType(glBindType);
            
            if(this.glBindType == null || this.glDrawType == null) {
                // Creates a string that indicates which values are null
                throw new NullPointerException(
                        (this.glBindType == null ? bindErr : "")
                      + (this.glDrawType == null ? drawErr : "")
                );
            }
        } catch (NullPointerException ex) {
            System.out.println("Could not match input to enumeration, setting necessary default values.");
            
            // Set to default whichever field was not configured correctly
            String errorStr = ex.toString();
            // Bind getType
            if(errorStr.contains(bindErr)) {
                System.err.println("glBindType does not match an enumerated type");
                this.glBindType = DEFAULT_BIND;
                
            // Draw getType
            } else if (errorStr.contains(drawErr)) {
                System.err.println("glDrawType does not match an enumerated type");
                this.glDrawType = DEFAULT_DRAW;
            }
        }
    }
    
    public int getDrawType() {
        return this.glDrawType.GL_DRAW;
    }
    
    public int getBindType() {
        return this.glBindType.GL_BIND;
    }
    
    public ByteBuffer getBuffer() {
        return this.buffer;
    }
    
    /**
     * 
     * @return 
     */
    public BufferType getType(){
        return this.type;
    }
    
    public int size() {
        return this.bufferSize;
    }
    
    private boolean isEmpty() {
        return this.bufferSize < 1;
    }
    
}
