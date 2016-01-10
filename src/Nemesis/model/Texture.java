package Nemesis.model;

import Nemesis.GameLoop;
import java.io.File;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

// Static imports, assuming ctrl-shift-i doesn't overwrite
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;
import static java.awt.image.BufferedImage.*;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

/**
 * Defines an image texture to be applied to a mesh.
 * @author Nathan
 */
public class Texture implements GameLoop {
    public static int DEFAULT_WIDTH = 1024;
    public static int DEFAULT_HEIGHT = 1024;
    
    // The image file for the texture
    BufferedImage texImage;
    IntBuffer pixelBuffer;
    
    // Texture image  width
    private int width;
    private int height;
    
    // Other texture properties
    private int borderWidth;
    private int levelOfDetail;
    
    // The OpenGL id for this texture
    private int texID;
    
    public Texture(File imageFile) {
        // Check file is not a directory
        if(imageFile.isFile() == false) {
            throw new IllegalArgumentException("Image file is not a normal file.");
        } 
        
        // Generate texImage
        try{
            texImage = ImageIO.read(imageFile);
            this.width = texImage.getWidth();
            this.height = texImage.getHeight();
        } catch(IOException ex) {
            System.err.println("Unable to read image file. " + ex.toString());
            texImage = new BufferedImage(DEFAULT_WIDTH, DEFAULT_HEIGHT, TYPE_INT_ARGB);
        }
        
        // Set some texture properties
        this.borderWidth = 0; // Always set to 0
        this.levelOfDetail = 0;
        this.pixelBuffer = getBuffer(this.texImage);
        
        // Set some OpenGL properties
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        
        this.texID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texID);
        texImage.getSampleModel();
        glTexImage2D(GL_TEXTURE_2D, levelOfDetail, GL_RGBA, this.width, this.height,
                borderWidth, GL_RGBA, GL_UNSIGNED_BYTE, pixelBuffer);
        glGenerateMipmap(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texID);
    }

    public void unbind() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void update() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void render() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cleanup() {
        glDeleteTextures(this.texID);
    }
    
    public int getID() {
        return this.texID;
    }
    
    private IntBuffer getBuffer(BufferedImage image) {
        IntBuffer buffer;
        Raster raster = image.getData();
        int rasterWidth = raster.getWidth();
        
        // Get pixels from image
        int[] pixels = new int[rasterWidth * raster.getNumBands() * raster.getHeight()];
        raster.getPixels(raster.getMinX(), raster.getMinY(), rasterWidth, raster.getHeight(), pixels);
        // Checks size of pixel array only if pixel array is not null
        if(pixels != null ? pixels.length == 0 : true) {
            throw new RuntimeException("Unable to read pixels from image.");
        }

        buffer = (IntBuffer) BufferUtils.createIntBuffer(pixels.length).put(pixels).flip();
        return buffer;

    }
    
}
