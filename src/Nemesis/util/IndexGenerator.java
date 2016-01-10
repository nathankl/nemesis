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

/**
 * Generates an index, then increments. Stupid simple.
 * @author Nathan
 */
public class IndexGenerator {
    
    private long index;
    
    public IndexGenerator() {
        this.index = 0;
    }
    
    /**
     * Generates a new index every time it is called. The index created is not
     * application unique, only unique from the instance of this class.
     * @return an index
     * @throws RuntimeException
     */
    public long getIndex() {
        if(index + 1 > Long.MAX_VALUE) {
            throw new RuntimeException("Index required is greater than maximum value of Long.");
        }
        return index++;
    }
}
