/*
 * Copyright (C) 2015 Nathan
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

import Nemesis.util.IndexGenerator;
import java.util.List;
import java.util.Vector;

/**
 * The Shader's source elements.
 * @author Nathan
 */
public class ShaderSource {
    // Number of shader elements in this class
    private IndexGenerator indexGen = new IndexGenerator();
    
    private enum ShaderElementType {
        BOOL,
        INT,
        UINT,
        FLOAT,
        DOUBLE,
        BVECN,
        IVECN,
        UVECN,
        VECN,
        DVECN,
        MATN,
        MATNXM;
    }
    private enum ShaderAccessType {
        UNIFORM,
    }
    private class ShaderElement {
        // Name of variable in shader source
        private String name;
        // The type of variable
        private ShaderElementType type;
        private ShaderAccessType access;
        // The class-unique index of the element
        private long index;
        
        public ShaderElement(String name, ShaderElementType type) {
            this.name = name;
            this.type = type;
            
            this.index = indexGen.getIndex();
        }
        
        public String getName() {
            return this.name;
        }

        public long getIndex() {
            return this.index;
        }
        
        public ShaderElementType getType() {
            return type;
        }
        
    }
    
    /**
     * Types of inputs that do not
     */
    private class ShaderOpaque {
        
    }
    
    private List<ShaderElement> elements;
    private List<ShaderOpaque> opaques;

    /**
     * Creates the source object from the source string
     */
    public ShaderSource(String source) {
        // Check source input
        
        // Parse input
    }
    
    public boolean hasUniform(String uniformName) {
        for(ShaderElement e : elements) {
            if(e.name.equals(uniformName) && e.type == ShaderDataType){
                return true;
            }
        }
    }
    
}
