package Nemesis.model;

import Nemesis.GameLoop;
import Nemesis.util.FileTools;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Model implements GameLoop {

    /*
    // v X Y Z ... W is optional
    public static final int VERTEX_COORD_COUNT = 3;
    // v X Y Z ... W is optional
    public static final int NORMAL_COORD_COUNT = 3;
    // v U V ... W is optional
    public static final int TEXTURE_COORD_COUNT = 2;
    
    public static final String MODEL_OBJECT_FILE_TYPE = ".obj";
    public static final char OBJ_FACE_INDICE_DELIMITER = '/';
    public static final char OBJ_FACE_ELEMENT_DELIMITER = ' ';
*/
    
    public static final File MODEL_DIR = new File(System.getProperty("user.dir") + "/models/");
    public static final File TEXTURE_DIR = new File(MODEL_DIR + "/textures/");
    
    /*
    // VAO position for storing buffer objects
    private static final int VERTICES_ATTRIB_POS = 0;
    private static final int NORMALS_ATTRIB_POS = 1;
    private static final int TEXTURES_ATTRIB_POS = 2;
    
    // Model data buffers
    private FloatBuffer vertices;
    private FloatBuffer normals;
    private FloatBuffer textures;
    private IntBuffer faceIndices;
    
    // Model Vertex Array Object ID
    private int vaoID, eboID;
    
    //  A list of which attribute lists are active for the object
    private List<Integer> attribList;
    
    // Model data fields
    private int vertexCount;
    private int indiceCount;
    
    // Texture of the object
    //private Texture texture;

    // Lists for all vaos and vbos of the object
    private static ArrayList<Integer> modelVaoIDs = new ArrayList<>();
    private static ArrayList<Integer> modelVboIDs = new ArrayList<>();
*/    
    //List<Mesh> meshList = new ArrayList<>();
    Map<String, Mesh> meshes = new HashMap<>();
    
    public Model(String modelFileName) {
        this(new File(FileTools.MODEL_DIR, modelFileName));
    }
    
    public Model(File modelFile) {
        // Read meshes from obj file
        this.getMeshesFromObjFile(modelFile).stream().forEach((m) -> {
            //meshList.add(m);
            meshes.put(m.getName(), m);
        });
        /*
        FileTools.readMeshesFromObjFile(modelFile).stream().forEach((m) -> {
            meshList.add(m);
        });
        */
    }
    
    private List<Mesh> getMeshesFromObjFile(File objFile) {
        ArrayList<Mesh> meshList = new ArrayList<>();
    }
    
    /**
     * Loads vertex information from an .obj file. 
     * @param modelFile The file that holds the .obj data.
     */
    /*
    public Model(File modelFile) {
        // Load model data from file
        loadDataFromFile(modelFile);
        this.attribList = new ArrayList<>();
        this.vertexCount = this.vertices.capacity() / VERTEX_COORD_COUNT;
        this.indiceCount = this.faceIndices.capacity();
        
        // Create an array that contains the model's data buffers
        ArrayList<FloatBuffer> data = new ArrayList<>();
        data.add(VERTICES_ATTRIB_POS, this.vertices);
        data.add(NORMALS_ATTRIB_POS, this.normals);
        data.add(TEXTURES_ATTRIB_POS, this.textures);
        
        // Create VAO and bind it
        this.vaoID = glGenVertexArrays();
        modelVaoIDs.add(this.vaoID);
        glBindVertexArray(this.vaoID);
        
        // Create VBOs and store data
        IntBuffer vbos = BufferUtils.createIntBuffer(3);
        glGenBuffers(vbos);
        for(int i = 0; i < vbos.capacity(); i++) {
            int v = vbos.get(i);
            FloatBuffer d = data.get(i);
            modelVboIDs.add(v);
            glBindBuffer(GL_ARRAY_BUFFER, v);
            glBufferData(GL_ARRAY_BUFFER, d, GL_STATIC_DRAW);
            if(d.capacity() > 0) {
                attribList.add(i);
                glVertexAttribPointer(i,
                        i == TEXTURES_ATTRIB_POS ? TEXTURE_COORD_COUNT : VERTEX_COORD_COUNT,
                        GL_FLOAT, false, 0, 0);
            }
        }
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        // Create EBO and store data
        eboID = glGenBuffers();
        modelVboIDs.add(eboID);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, faceIndices, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        // Unbind VAO
        glBindVertexArray(0);
        
        // Create texture data
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    }
    */

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void update() {
        
    }
    
    /**
     * Render every mesh in the model.
     */
    @Override
    public void render() {
        meshList.stream().forEach((Mesh m) -> m.render());
    }
    
    @Override
    public void cleanup() {
        // Cleanup every mesh
        meshList.stream().forEach((Mesh m) -> m.cleanup());
    }

    /**
     * Combines the vertex count for every mesh in the model.
     * @return all the vertices of every mesh in the model.
     */
    public int getVertexCount() {
        int vert = 0;
        return meshList.stream().map((m) ->
            m.getVertexCount()
        ).reduce(vert, Integer::sum);
    }
    
    /**
     * Finds first occurrence of a Mesh with meshName in the Model's mesh list.
     * User should check for a null return value.
     * @param meshName the name of the mesh to find
     * @return a mesh object or null if not found
     */
    public Mesh getMesh(String meshName) {
        return meshes.get(meshName);
    }
 
    /**
     * Gets the names of all the meshes in the model.
     * @return a list of names or null if error
     */
    public ArrayList<String> getMeshNames() {
        if(this.meshes == null) {
            throw new NullPointerException();
        } else if(this.meshes.size() < 1) {
            throw new IllegalStateException("Model has no meshes in it.");
        } 
        
        List<String> names = new ArrayList<>();
        for(Entry e : meshes.entrySet()) {
            names.add((String) e.getKey());
        }
        
        if(names == null) {
            throw new RuntimeException("No mesh names found in model.");
        }
    }
    
    /*
    public int getVaoID() {
        return this.vaoID;
    }
    
    public int indiceCount() {
        return this.indiceCount;
    }
    
    public IntBuffer getIndice() {
        return this.faceIndices;
    }
    */
    
    /**
     * Binds the VAO, EBO, and enables all the attribute lists
     */
    /*
    private void bind() {
        glBindVertexArray(vaoID);
        attribList.stream().forEach((attribPos) -> {
            glEnableVertexAttribArray(attribPos);
        });
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
    }
    */
    
    /**
     * Unbind the VAO, EBO, and disables all attribute lists
     * in reverse order of the calls in bind()
     */
    /*
    private void unbind() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        attribList.stream().forEach((attribPos) -> {
            glDisableVertexAttribArray(attribPos);
        });
        glBindVertexArray(0);
    }
    */
    /**
     * Print a matrix style view of the buffers:
     * vertices, normals, textures & faceIndices
     */
    /*
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
        for(int i = 0; i < this.textures.capacity(); i++) {
            System.out.print(textures.get(i) + ", ");
            if(i % 2 == 1) {
                System.out.println();
            }
        }
        System.out.println();

        // Print face indices
        System.out.println("Indices Buffer:");
        for(int i = 0; i < this.faceIndices.capacity(); i++) {
            System.out.print(faceIndices.get(i) + ", ");
            if(i % 3 == 2) {
                System.out.println();
            }
        }
        System.out.println();
    }
    */
    /**
     * Reads a list of arrays from a string. Ensure a non-empty array before using.
     * @param elementCount Number of elements to extract from the string.
     * @param intArrayStr The String containing the array of integers to be
     * parsed. This string should be '.obj' format.
     * @return An ArrayList containing 3 integer arrays. Vertex indices, 
     * vertex texture indices, and normals indices. Respectively, starting with
     * the base index of 0.
     */
    /*
    private ArrayList<int[]> getInts(int elementCount, String intArrayStr) {
        int[] vert, text, norm;
        vert = new int[elementCount];
        text = new int[elementCount];
        norm = new int[elementCount];
        ArrayList<int[]> resultList = new ArrayList<>();
        
        // Which array to assign value to
        // vert = 0, text = 1, norm = 2
        int indice = 0;
        
        // Which element is being parsed
        int element = 0;

        // Parse strings
        String e = "";
        
        // Iterate over string
        for (int i = 0; i < intArrayStr.length(); i++) {
            char c = intArrayStr.charAt(i);
            
            // Add character to parse string
            if(Character.isDigit(c)) {
                e += c;
            }
            
            // Parse string and increase indice count
            if (c == OBJ_FACE_INDICE_DELIMITER || c == OBJ_FACE_ELEMENT_DELIMITER
                    || i + 1 == intArrayStr.length()) {
                try {
                    // Sometimes parse if indice delimiter is found
                    if(e.isEmpty() == false) {
                        // '.obj' indice starts with 1, opengl indices uses a base of 0
                        // So decrement the parsed integer by 1
                        switch(indice) {
                            case 0:
                                vert[element] = Integer.parseInt(e) - 1;
                                break;
                            case 1:
                                text[element] = Integer.parseInt(e) - 1;
                                break;
                            case 2:
                                norm[element] = Integer.parseInt(e) - 1;
                                break;
                        }
                        

                        
                        // Reset parse string after parse attempt
                        e = "";
                        // Increment element only if last indice in element
                        element = indice == 2 ? element + 1 : element;
                        // Increment indice index
                        indice = indice < 2 ? indice + 1 : 0;
                        
                    // Always increment indice upon indice delimiter
                    // Note: Checks twice if found indice delimiter
                    // Once above, once here
                    } else if(c == OBJ_FACE_INDICE_DELIMITER) {
                        indice = indice < 2 ? indice + 1 : 0;
                    }
                    
                } catch (NumberFormatException ex) {
                    System.err.println("Could not parse integer. "
                            + ex.toString());
                }
            }
        }
        
        // Add integer arrays to arraylist
        resultList.add(vert);
        resultList.add(text);
        resultList.add(norm);
        
        return resultList;
    }
    */
    
    /**
     * Reads an array of floats from a string containing floats.
     * @param elementCount Number of elements to extract from floatArrayStr.
     * @param floatArrayStr The String that contains floats.
     * @return An array of floats that indicate a coordinate position.
     */
    /*
    private float[] getFloats(int elementCount, String floatArrayStr) {
        float[] fa = new float[elementCount];
        String f = "";
        
        // Iterate over string
        for (int it = 0, in = 0; it < floatArrayStr.length(); it++) {
            char c = floatArrayStr.charAt(it);
            
            // If c is part of float value, add it
            if(Character.isDigit(c) || c == '.' || c == '-') {
                f += c;
                
            }
            
            // If encounter white space or end of line
            if((c == ' ' || it + 1 == floatArrayStr.length()) && f.isEmpty() == false) {
                try {
                    fa[in++] = Float.parseFloat(f);
                    f = "";
                    
                // If this goes bad, could throw thousands of exceptions
                // depending on the size of the object file
                } catch (NumberFormatException ex) {
                    System.err.println("Could not parse float. " + ex.toString());
                }
            }
        }
        
        return fa;
    }
    */
    
    /*
    private void loadDataFromFile(File modelFile) {
        ArrayList<Float> verts = new ArrayList<>();
        ArrayList<Float> norml = new ArrayList<>();
        ArrayList<Float> textr = new ArrayList<>();
        ArrayList<Integer> vertsInd = new ArrayList<>();
        ArrayList<Integer> normlInd = new ArrayList<>();
        ArrayList<Integer> textrInd = new ArrayList<>();
        
        try {
            // Assuming modelFile is a valid file
            if(modelFile.isFile() == false) {
                throw new FileNotFoundException("Model file "
                        + modelFile.getPath() + " is not a 'normal file'.");
            }
            
            // Assuming modelFile is an OBJ file
            String ext = FileTools.getFileExtension(modelFile.toString());
            if(ext.equals(MODEL_OBJECT_FILE_TYPE) == false) {
                throw new Exception("Object file extension " + ext
                        + " is unsupported.");
            }
            
            // Create a reader
            BufferedReader reader = new BufferedReader(
                    new FileReader(modelFile)
            );
            
            // Read lines
            String line;
            float[] floats;
            while ((line = reader.readLine()) != null) {
                switch(line.charAt(0)) {
                    // Get vertex data
                    case 'v':
                        switch(line.charAt(1)) {
                            // v Vertices
                            case ' ':
                                // Get float array
                                floats = getFloats(VERTEX_COORD_COUNT, 
                                        line.substring(1));
                                
                                // Add float array to vertices arrayList
                                for (int i = 0; i < floats.length; i++) {
                                    verts.add(floats[i]);
                                }
                                break;

                            // vn Vertex normals
                            case 'n':
                                // Get float array
                                floats = getFloats(NORMAL_COORD_COUNT,
                                        line.substring(2));
                                
                                // Add float array to normals ArrayList
                                for (int i = 0; i < floats.length; i++) {
                                    norml.add(floats[i]);
                                }
                                break;

                            // vt Texture coordinates
                            case 't':
                                // Get float array
                                floats = getFloats(TEXTURE_COORD_COUNT,
                                        line.substring(2));
                                
                                // Add float array to texture ArrayList
                                for (int i = 0; i < floats.length; i++) {
                                    textr.add(floats[i]);
                                }
                                break;

                            // vp Free-form curve/surface vertices
                            case 'p':
                                // NOT implemented, only here for completeness
                                break;
                        }
                        break;

                    // Get face element data
                    case 'f':
                        // Get int array arraylist
                        ArrayList<int[]> list = getInts(VERTEX_COORD_COUNT, line);
                        int[] v, t, n;
                        
                        // Get arrays from arraylist
                        v = list.get(0);
                        t = list.get(1);
                        n = list.get(2);
                        
                        // Add arrays to indices
                        // Vertices indices
                        for (int i = 0; i < v.length; i++) {
                            vertsInd.add(v[i]);
                            
                        }
                        
                        // Texture indices
                        for (int i = 0; i < t.length; i++) {
                            textrInd.add(t[i]);
                            
                        }
                        
                        // Normals indices
                        for (int i = 0; i < n.length; i++) {
                            normlInd.add(n[i]);
                            
                        }
                        
                        break;
                }
            }
            
        } catch (FileNotFoundException ex) {
            System.err.println("Could not load object file. " + ex.toString());
            
        } catch (IOException ex) {
            System.err.println(ex.toString());
            
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }
        
        // Put loaded data from ArrayList into Buffer objects
        int vertsSize = verts.size();
        this.vertices = BufferUtils.createFloatBuffer(vertsSize);
        for (int i = 0; i < vertsSize; i++) {
            vertices.put(i, verts.get(i));
        }
        
        int normlSize = norml.size();
        this.normals = BufferUtils.createFloatBuffer(normlSize);
        for (int i = 0; i < normlSize; i++) {
            normals.put(i, norml.get(i));
        }
        
        int textrSize = textr.size();
        this.textures = BufferUtils.createFloatBuffer(textrSize);
        for (int i = 0; i < textrSize; i++) {
            textures.put(i, textr.get(i));
        }
        
        // TODO: Put other data int EBO
        
        // Put only vertex indice into this buffer for now
        int vertsIndSize = vertsInd.size();
        this.faceIndices = BufferUtils.createIntBuffer(vertsIndSize);
        for (int i = 0; i < vertsIndSize; i++) {
            faceIndices.put(i, vertsInd.get(i));
            
        }
    }
    */
    
    /*
    private void move(float xStep, float yStep) {
        float xMin, xMax, yMin, yMax;
        
        xMin = DisplayManager.WINDOW_X_MIN;
        xMax = DisplayManager.WINDOW_X_MAX;
        yMin = DisplayManager.WINDOW_Y_MIN;
        yMax = DisplayManager.WINDOW_Y_MAX;
        
        // Create the copy buffer
        // TODO: Maybe ArrayList would be better than FloatBuffer
        FloatBuffer moveTo = ByteBuffer.allocateDirect(vertices.capacity() * Float.BYTES)
                .asFloatBuffer();
        for(int i = 0; i < moveTo.limit(); i++) {
            moveTo.put(vertices.get());
        }
        
        // Set moveTo buffer ready to write to
        moveTo.flip();
        vertices.rewind();
        
        // Loop through the copied buffer and increment vertices
        float x, y, xTo, yTo;
        boolean isInBounds = true;
        for(int i = 0; i < moveTo.limit(); i+= 3) {
            
            // Define the x and y next coordinates
            x = moveTo.get(i);
            y = moveTo.get(i + 1);
            xTo = x + xStep;
            yTo = y + yStep;
            
            // If out of bounds
            if (xTo > xMax || xTo < xMin) {
                isInBounds = false;
                move(x > 0.0f ? xMax - x : xMin - x, yStep);
                break;
            }
            if ( yTo > yMax || yTo < yMin) {
                isInBounds = false;
                move(xStep, y > 0.0f ? yMax - y : yMin - y);
                break;
            }
            
            // Increment the x and y coordinate
            moveTo.put(i, xTo);
            moveTo.put(i + 1, yTo);
        }
        moveTo.rewind();
        
        if(isInBounds) {
            vertices.put(moveTo);
            vertices.flip();
        }
    }
    
    public void moveX(float step) {
        move(step, 0.0f);
    }
    
    public void moveY(float step) {
        move(0.0f, step);
    }
    */
}
