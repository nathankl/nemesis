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

import java.awt.Color;
import java.awt.Transparency;

/**
 * Defines a material as applied to a mesh.
 * @author Nathan
 */
public class Material {
    private Color ambient;
    private Color diffuse;
    private Color specular;
    private float specWeight;
    private float transparency;
    
    private Texture ambientTex;
    private Texture diffuseTex;
    private Texture specularColorTex;
    private Texture specularHighlightTex;
    private Texture alphaTex;
    
    private Texture bumpMap;
    private Texture illuminationMap;
    private Texture decalTex;
    
    public enum illumination {
        COLOR_NOT_AMBIENT,
        COLOR_AND_AMBIENT,
        HIGHLIGHT,
        REFLECTION_AND_RAYTRACE,
        GLASS_AND_RAYTRACE,
        FRESNEL_AND_RAYTRACE,
        REFRACTION_AND_RAYTRACE_NOT_FRESNEL,
        REFRACTION_AND_RAYTRACE_AND_FRESNEL,
        REFLECTION_NOT_RAYTRACE,
        GLASS_NOT_RAYTRACE,
        SHADOW_CAST_TO_INVISIBLE
    }
    
    // TODO
    public Material() {
        
    }
    
    public void setTransparency(float val) {
        if(val < 0.0f || val > 1.0f) {
            throw new IllegalArgumentException("Value specified for transparency is not 0.0 - 1.0.");
        }
        
        this.transparency = val;
    }
}
