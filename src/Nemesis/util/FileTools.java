package Nemesis.util;

import Nemesis.model.Obj;
import Nemesis.model.Material;
import Nemesis.model.Mesh;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileTools {
    // Object file types
    public static final String OBJ_FILE_EXT = ".obj";
    public static final String MTL_FILE_EXT = ".mtl";
    
    // File directories
    public static final File MODEL_DIR = new File(System.getProperty("user.dir") + "/models/");
    public static final File MATERIAL_DIR = new File(System.getProperty("user.dir") + "/models/");
    
    public static String readTextFile(File file) {
        // Default to empty string
        String str = "";
        
        try {
            
            // Create reader
            String line;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            
            // Read lines into string builder
            stringBuilder.append(reader.readLine());
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            
            // Output the built string to source string
            str = stringBuilder.toString();
            
        } catch (FileNotFoundException e) {
            System.err.println("Could not load text file. " + e.toString());
            
        } catch (IOException e) {
            System.err.println(e.toString());
            
        }
        
        return str;
        
    }
    
    /**
     * Reads the file extension of the file. Note that it does not account for a
     * file that has two extensions. For example, "*.tar.gz" will return simply
     * ".gz".
     * @param file
     * @return The extension of the file. If no extension is found, returns an
     * empty string.
     */
    public static String getFileExtension(File file) {
        String filePath = file.toString();
        String ext = "";
        
        int index = filePath.lastIndexOf('.');
        // Sets extension only if an extension is found
        if(index >= 0) {
            ext = filePath.substring(index);
        }
        
        return ext;
    }
    
    /**
     * Reads obj material data from an mtl file. TODO
     * @param file the mtl file
     * @return a list of materials loaded from the file
     */
    public static Material readMaterialFromMtlFile(File file) {
        // Check state of program and arguments
        if(file.isFile() == false) {
            throw new IllegalArgumentException("Material file " + file.getPath() + " is not a normal file.");
        }
        if(getFileExtension(file).equals(MTL_FILE_EXT) == false) {
            throw new IllegalArgumentException("Model file " + file.getPath() + " is not an " + MTL_FILE_EXT + " file.");
        }
        
        return new Material();
    }
    
    /**
     * Reads comments and material library names then passes all vertex data to
     * {@link Nemesis.util.Obj#readObject }. Calls {@link Nemesis.util.FileTools#readMaterialFromMtlFile }
     * with every mtllib name extracted from obj file and adds the created 
     * material to the object.
     * @param file the obj file. Throws IllegalArgumentException if is not a
     * normal file or if it is not an .obj file.
     * @return a list of meshes loaded from file. If no meshes were loaded, then
     * it will return an empty list.
     * @throws IllegalArgumentException
     */
    public static List<Mesh> readMeshesFromObjFile(File file) {
        // Check state and arguments
        if(file.isFile() == false) {
            throw new IllegalArgumentException("Model file " + file.getPath() + " is not a normal file.");
        }
        if(getFileExtension(file).equals(OBJ_FILE_EXT) == false) {
            throw new IllegalArgumentException("Model file " + file.getPath() + " is not an " + OBJ_FILE_EXT + " file.");
        }

        // The list of meshes to return
        List<Mesh> meshes = new ArrayList<>();
        String meshName = "";

        // Obj file comments
        List<String> comments = new ArrayList<>();
        
        // TODO: implement mtllib
        // A list of material file names
        List<String> materialLib = new ArrayList<>();
        
        
        // Read lines from obj file
        try {
            // Create a reader
            BufferedReader reader = new BufferedReader(new FileReader(file));
            
            // Create list of strings
            List<String> lineList = new ArrayList<>();
            String line;
            
            while((line = reader.readLine()) != null) {
                // Checks for comments
                if(line.indexOf(Obj.OBJ_FILE_COMMENT) == 0) {
                    comments.add(line);

                // Checks for material lib
                } else if(line.indexOf(Obj.OBJ_FILE_MATERIAL) == 0) {
                    materialLib.add(Obj.readMaterialLibrary(line));

                // Reads a mesh from the object lines
                } else if(line.indexOf(Obj.OBJ_FILE_OBJECT) == 0 && lineList.isEmpty() == false) {
                    // Create mesh
                    Mesh mesh = Obj.readObject(lineList);
                    if(materialLib.isEmpty() == false) {
                        for(String libname : materialLib) {
                            mesh.addMaterial(readMaterialFromMtlFile(new File(MATERIAL_DIR, libname)));
                        }

                    }
                    
                    // Add initialized mesh to list
                    meshes.add(mesh);
                    
                    // Reset line list
                    lineList.clear();
                    
                    // Add object line to next object string
                    lineList.add(line);
                    
                // Add uncaught lines to object string
                } else {
                    lineList.add(line);
                }

            }
            
            // Creates another mesh at the end of the file
            if(lineList.toString().isEmpty() != true) {
                meshes.add(Obj.readObject(lineList));
                lineList.clear();
            }
            
        } catch (FileNotFoundException ex) {
            System.err.println(ex.toString());
        } catch(IOException ex) {
            System.err.println(ex.toString());
        }
        
        // Return meshes
        return meshes;
    }
}
