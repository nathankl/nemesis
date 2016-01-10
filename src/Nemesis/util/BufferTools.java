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
package Nemesis.util;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import java.util.List;
import org.lwjgl.BufferUtils;

/**
 * Collection of handy buffer tools.
 *
 * @author Nathan
 */
public class BufferTools {

    public BufferTools() {

    }

    /**
     * Creates a FloatBuffer from a List.
     * @param list a list of floats
     * @return a FloatBuffer created from the list data
     */
    public static FloatBuffer floatBufferFromList(List<Float> list) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(list.size());
        list.stream().forEach((f) -> {
            buffer.put(f);
        });
        buffer.rewind();
        return buffer;
    }
    
    public static IntBuffer intBufferFromList(List<Integer> list) {
        IntBuffer buffer = BufferUtils.createIntBuffer(list.size());
        list.stream().forEach((f) -> {
            buffer.put(f);
        });
        buffer.rewind();
        return buffer;
    }

    /*
    // TODO: Create generic function of bufferFromList
    public static <B extends Buffer, N extends Number> B bufferFromList(List<N> list) {

    }
    */
}
