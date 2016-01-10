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
package Nemesis.model;

import Nemesis.util.BufferTools;
import Nemesis.util.FileTools;
import java.util.ArrayList;
import java.util.List;

/**
 * Methods for reading and loading object files.
 * @author Nathan
 */
public class Obj {
    /**
     * Wraps a line from an object
     */
    public static class Line {
        /**
         * Defines the types of lines in an obj file
         */
        public enum Type {
            NAME("o"),
            MATERIAL("mtllib"),
            USE_MATERIAL("usemtl"),
            VERTEX("v"),
            VERTEX_NORMAL("vn"),
            VERTEX_TEXTURE("vt"),
            VERTEX_FACE_INDICE("f"),
            COMMENT("f"),
            SMOOTH_SHADING("s");

            private String linePrefix;
            Type(String linePrefix) {
                this.linePrefix = linePrefix;
            }

            public String getPrefix() {
                return this.linePrefix;
            }

            /**
             * Gets the type of data held in the obj line.
             * @param line a line from the object file
             * @return the type of values indicated by line, or null if no match
             */
            public static Type getLineType(String line) {
                Type type = null;
                for(Type t : Type.values()){
                    // If line has the matching type's line prefix followed by a space
                    // and is at the beginning of the line
                    if(line.indexOf(t.linePrefix + " ") == 0) {
                        type = t;
                        break;
                    }
                }
                assert(type != null);
                return type;
            }
        }

        private Type type;
        private String prefix;
        private String line;
        
        /**
         * 
         * @param objLine a valid line from an Obj File
         */
        public Line(String objLine) {
            // Assuming line is greater than 0
            if(objLine.length() < 1) {
                throw new IllegalArgumentException("Line is zero length: ");
            }
            //assert(objLine.length() > 0);
            
            this.type = Type.getLineType(objLine);
            // Assuming type of line is valid
            if(this.type == null) {
                throw new IllegalArgumentException("Line is not a valid line from an .obj file: "
                        + objLine + "\n Could not figure the type of data.");
            }
            //assert(this.type != null);
            
            
            this.prefix = this.type.getPrefix();
            // Assuming prefix is non zero
            if(this.prefix.length() < 0) {
                throw new RuntimeException("Couldn't get a match for the obj line prefix.");
            }
            //assert(this.prefix.length() > 0);

            this.line = objLine.substring(this.prefix.length());
        }
        
        public Type getType() {
            return this.type;
        }
        
        /**
         * Gets the string information in a line. Since lines are already separated
         * into their line data and prefix name, only have to return the line data.
         * @return the String data held in the .obj line
         */
        public String readString() {
            return line;
        }
        
        /**
         * Parses multiple floats from a string and formats them into a list.
         * @param count the number of floats to extract from the string
         * @return a list of floats
         */
        private List<Float> readFloats(int count) {
            List<Float> floats = new ArrayList<>(count);
            String substr = "";

            // Iterates over line
            for(int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);

                // Adds the character if it is part of float value
                if(Character.isDigit(c) || c == '.' || c == '-') {
                    substr += c;
                }

                // Parses the float if white space or end of line is encountered
                if((c == ' ' || i + 1 == line.length()) && substr.isEmpty() == false) {
                    try {
                        floats.add(Float.parseFloat(substr));
                        substr = "";

                    } catch (NumberFormatException ex) {
                        System.err.println("Could not parse Float. " + ex.toString());
                    }
                }
            }

            return floats;
        }
        /**
         * Reads vertex, texture, and normal indices from an obj line.
         * @return a list of lists. [vertexList, textureList, normalList]
         */
        public List<List<Integer>> readFaceIndices() {
            // List of lists to return
            List<List<Integer>> indices = new ArrayList<>();

            // List of every parsed int
            List<Integer> parsed = new ArrayList<>();

            // Substring ... obviously
            String substr = "";

            // Iterates over line
            assert(line.length() > 0);
            for(int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);

                // Checks if substr should be parsed
                if(c == '/' || c == ' ' || i + 1 == line.length()) {
                    // Parse integer if substr is not empty
                    if(substr.isEmpty() == false) {
                        try {
                            parsed.add(Integer.parseInt(substr));
                            substr = "";
                        } catch (NumberFormatException ex) {
                            System.err.println("Could not parse Integer. " + ex.toString());
                            System.err.println("Supplying 0.");
                            parsed.add(0);
                        }

                    // Adds a zero to the parsed list as a placeholder
                    } else {
                        parsed.add(0);
                    }

                // Adds character to substring if it's a digit
                } else if(Character.isDigit(c)) {
                    substr += c;
                }
            }

            // Size of parsed ArrayList should be 9
            assert(parsed.size() == 9);

            // Reformat the single list into a list of lists
            int listSize = 3;
            for(int i = 0; i < listSize; i++) {
                List<Integer> sublist = new ArrayList();
                sublist.add(parsed.get(i));
                sublist.add(parsed.get(i + 3));
                sublist.add(parsed.get(i + 6));
                indices.add(sublist);
            }

            return indices;
        }

    }
    
    public static class File {
        private static final String OBJ_FILE_EXT = ".obj";
        
        public File(java.io.File file) {
            if(file.isFile() == false) {
                throw new IllegalArgumentException("File specified is not a normal file: " + file.getPath());
            } else if(FileTools.getFileExtension(file).equals(OBJ_FILE_EXT) == false) {
                throw new IllegalArgumentException("File specified does not have " + OBJ_FILE_EXT +
                        " file extension: " + file.getPath());
            }
        }
    }
    
    public static class Data {
        public enum Type {
            VERTEX(3),
            TEXTURE(2),
            NORMAL(3);
            
            public final int coordCount;
            Type(int coordCount) {
                this.coordCount = coordCount;
            }
        }
        
        public static class Faces {
            public static class Face {
                private Float[] vertexCoord;
                private Float[] textureCoord;
                private Float[] normal;
                
                public Face() {
                    this.vertexCoord = new Float[Type.VERTEX.coordCount];
                    this.vertexCoord = new Float[Type.TEXTURE.coordCount];
                    this.normal = new Float[Type.NORMAL.coordCount];
                }
            }
            
            List<Face> faces;
            
            public Faces() {
                faces = new ArrayList<>();
            }
            
            public List<Face> getFaces() {
                return this.faces;
            }
            
            private void addVertexInd(List<Integer> vertexInd) {
                
            }
            private void addTextureInd(List<Integer> textureInd) {
                
            }
            private void addNormalInd(List<Integer> normalInd) {
                
            }
        }
        
        // The data that is held in Obj.Data
        private List<Float>  vertexCoords;
        private List<Float> textureCoords;
        private List<Float>  normalCoords;
        private Faces faces;
        private String name;
        private String material;
        private String materialLibrary;
        private String comments;
        private boolean smooth;
        
        /**
         * Gets data from File
         * @param lines
         */
        public Data(List<Line> lines) {
            // Assuming lines is a valid list
            if(lines == null) {
                throw new NullPointerException();
            } else if(lines.size() < 1) {
                throw new IllegalArgumentException("Object line list is zero length.");
            }
            
            this.vertexCoords = new ArrayList<>();
            this.textureCoords = new ArrayList<>();
            this.normalCoords = new ArrayList<>();
            this.faces = new Faces();
            this.name = "";
            this.material = "";
            this.materialLibrary = "";
            this.comments = "";
            this.smooth = false;
            
            // Read the lines
            for(Line line : lines) {
                switch(line.getType()) {
                case NAME:
                    // Should not overwrite object name
                    if(name != null) {
                        throw new RuntimeException("Multiple object name definitions for single object.");
                    }
                    //assert(objName.equals(""));
                    name = line.readString();
                    break;
                    
                case MATERIAL:
                    materialLibrary = line.readString();
                    break;
                    
                case USE_MATERIAL:
                    material = line.readString();
                    break;
                    
                case VERTEX:
                    vertexCoords.addAll(line.readFloats(Type.VERTEX.coordCount));
                    break;
                    
                case VERTEX_NORMAL:
                    normalCoords.addAll(line.readFloats(Type.NORMAL.coordCount));
                    break;
                    
                case VERTEX_TEXTURE:
                    textureCoords.addAll(line.readFloats(Type.TEXTURE.coordCount));
                    break;
                    
                case VERTEX_FACE_INDICE:
                    List<List<Integer>> list = line.readFaceIndices();
                    faces.addVertexInd(list.get(0));
                    faces.addTextureInd(list.get(1));
                    faces.addNormalInd(list.get(2));

                    break;
                    
                case COMMENT:
                    this.comments += line.readString() + "\n";
                    break;
                
                case SMOOTH_SHADING:
                    // smooth equals true if smooth shading is set to "on"
                    smooth = line.readString().contains("on");
                    break;
                    
                default:
                    // Should NOT execute
                    assert(false);
                }
            }
        }
        
        public long getVertexCount() {
            return this.vertexCoords.size() / Type.VERTEX.coordCount;
        }
        
        public long getFaceCount() {
            return this.faces.;
        }
    }
    
    // the Obj.File object
    File file;
    // the list of Obj.Data held in 
    List<Data> dataList;
    
    public Obj(java.io.File modelFile) {
        this.file = new File(modelFile);
        this.dataList = new ArrayList();
        
        Line line = 
    }
    
    /**
     * Generates a single mesh object given lines from an obj file.
     * @param lines a list of the .obj lines
     * @return 
     */
    public static Mesh readObject(List<String> lines) {
        // Create empty lists to extract data into
        List<Float> verts = new ArrayList<>();
        List<Float> norml = new ArrayList<>();
        List<Float> textr = new ArrayList<>();
        List<Float> vertColor = new ArrayList<>();
        List<Integer> vertsInd = new ArrayList<>();
        List<Integer> textrInd = new ArrayList<>();
        List<Integer> normlInd = new ArrayList<>();
        String materialName = "";
        String objName = "";
        
        boolean smooth = false;
        ObjData objData = new ObjData();
        
        // Declares the wrapper object
        Line objLine;
        // Read line from buffered reader
        for(String line : lines) {
            objLine = new Line(line);

            // Read lines based on line type
            switch(objLine.getType()) {
                case NAME:
                    // Should not overwrite object name
                    assert(objName.equals(""));
                    objName = readName(line);
                    break;
                    
                case MATERIAL:
                    // Do nothing
                    break;
                    
                case USE_MATERIAL:
                    materialName = readUseMaterial(line);
                    break;
                    
                case VERTEX:
                    verts.addAll(readVertex(line));
                    break;
                    
                case VERTEX_NORMAL:
                    norml.addAll(readVertexNormal(line));
                    break;
                    
                case VERTEX_TEXTURE:
                    textr.addAll(readVertexTexture(line));
                    break;
                    
                case VERTEX_FACE_INDICE:
                    List<List<Integer>> list = readVertexFaceIndice(removePrefix(Obj.OBJ_FILE_INDICES, line));
                    vertsInd.addAll(list.get(0));
                    textrInd.addAll(list.get(1));
                    normlInd.addAll(list.get(2));
                    break;
                    
                case COMMENT:
                    // Do nothing
                    break;
                
                case SMOOTH_SHADING:
                    smooth = readSmoothShading(line);
                    break;
                    
                default:
                    // Should NOT execute
                    assert(false);
            }
        }
        ObjData obj = new ObjData(objName);
        obj.setVerts();
        obj.setTextr();
        obj.setNorml();
        /*
        obj.setVertsInd();
        obj.setTextrInd();
        obj.setNormlInd();
        */
        obj.setIndice();
        
        Mesh mesh = new Mesh(
                objName,
                BufferTools.floatBufferFromList(verts),
                BufferTools.floatBufferFromList(textr),
                BufferTools.floatBufferFromList(norml),
                BufferTools.intBufferFromList(vertsInd),
                BufferTools.intBufferFromList(textrInd),
                BufferTools.intBufferFromList(normlInd)
        );
        
        //mesh.setSmooth(smooth);
        
        return mesh;
    }
        
    /**
     * 
     * @param line
     * @return 
     */
    public static String readComment(String line) {
        return removePrefix(Obj.OBJ_FILE_COMMENT, line);
    }
        
    /**
     * Placeholder method.
     * @param line
     * @return 
     */
    public static String readMaterialLibrary(String line) {
        return removePrefix(Obj.OBJ_FILE_MATERIAL, line);
    }
    
    private static String removePrefix(String prefix, String line) {
        // Remove the prefix extra character, the space
        return line.substring(prefix.length());
    }
    
    /**
     * 
     * @param line
     * @return the name of the object
     */
    private static String readName(String line) {
        // Return the whole line, subtracting the line prefix
        return removePrefix(Obj.OBJ_FILE_OBJECT, line);
    }

    /**
     * 
     * @param line
     * @return the name of the material to use for this mesh
     */
    private static String readUseMaterial(String line) {
        return removePrefix(Obj.OBJ_FILE_USE_MATERIAL, line);
    }
    
    /**
     * 
     * @param line
     * @return a list of floats
     */
    private static List<Float> readVertex(String line) {
        return readFloats(Mesh.VERTEX_COORD_COUNT,
                removePrefix(Obj.OBJ_FILE_VERTEX, line));
    }
    
    /**
     * 
     * @param line
     * @return a list of floats
     */
    private static List<Float> readVertexNormal(String line) {
        return readFloats(Mesh.NORMAL_COORD_COUNT,
                removePrefix(Obj.OBJ_FILE_NORMAL, line));
    }
    
    /**
     * 
     * @param line
     * @return a list of floats
     */
    private static List<Float> readVertexTexture(String line) {
        return readFloats(Mesh.TEXTURE_COORD_COUNT,
                removePrefix(Obj.OBJ_FILE_TEXTURE, line));
        
    }

    /**
     * Checks if smooth shading is set to on or off
     * @param line an obj file line
     * @return true if line contains on, false otherwise
     */
    private static boolean readSmoothShading(String line) {
        return removePrefix(Obj.OBJ_FILE_SMOOTH, line).contains("on");
    }
    
    /**
     * Reads vertex, texture, and normal indices from an obj line.
     * @param line a line from an obj file. The line must have the prefix
     * removed and the face must have 3 vertices. More than 3 vertices will be
     * ignored.
     * @return a list of lists. [vertexList, textureList, normalList]
     */
    private static List<List<Integer>> readVertexFaceIndice(String line) {
        // List of lists to return
        List<List<Integer>> indices = new ArrayList<>();
        
        // List of every parsed int
        List<Integer> parsed = new ArrayList<>();
        
        // Substring ... obviously
        String substr = "";
        
        // Iterates over line
        assert(line.length() > 0);
        for(int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            // Checks if substr should be parsed
            if(c == '/' || c == ' ' || i + 1 == line.length()) {
                // Parse integer if substr is not empty
                if(substr.isEmpty() == false) {
                    try {
                        parsed.add(Integer.parseInt(substr));
                        substr = "";
                    } catch (NumberFormatException ex) {
                        System.err.println("Could not parse Integer. " + ex.toString());
                        System.err.println("Supplying 0.");
                        parsed.add(0);
                    }
                    
                // Adds a zero to the parsed list as a placeholder
                } else {
                    parsed.add(0);
                }
                
            // Adds character to substring if it's a digit
            } else if(Character.isDigit(c)) {
                substr += c;
            }
        }
        
        // Size of parsed ArrayList should be 9
        assert(parsed.size() == 9);

        // Reformat the single list into a list of lists
        int listSize = 3;
        for(int i = 0; i < listSize; i++) {
            List<Integer> sublist = new ArrayList();
            sublist.add(parsed.get(i));
            sublist.add(parsed.get(i + 3));
            sublist.add(parsed.get(i + 6));
            indices.add(sublist);
        }
        
        return indices;
    }

    /**
     * Parses multiple floats from a string and formats them into a list.
     * @param count the number of floats to extract from the string
     * @param line a String whose obj prefix has been removed, leaving only
     * vertex or mesh data. The floats must be separated by a space.
     * @return a list of floats
     */
    private static List<Float> readFloats(int count, String line) {
        List<Float> floats = new ArrayList<>(count);
        String substr = "";
        
        // Iterates over line
        for(int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            // Adds the character if it is part of float value
            if(Character.isDigit(c) || c == '.' || c == '-') {
                substr += c;
            }
            
            // Parses the float if white space or end of line is encountered
            if((c == ' ' || i + 1 == line.length()) && substr.isEmpty() == false) {
                try {
                    floats.add(Float.parseFloat(substr));
                    substr = "";
                
                } catch (NumberFormatException ex) {
                    System.err.println("Could not parse Float. " + ex.toString());
                }
            }
        }
        
        return floats;
    }
    
}
