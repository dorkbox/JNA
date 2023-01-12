/*
 * Copyright 2017 dorkbox, llc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dorkbox.jna.linux.structs;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

/**
 * https://developer.gnome.org/pango/stable/pango-Glyph-Storage.html#PangoRectangle
 */
public
class PangoRectangle extends Structure {

    public int x;
    public int y;
    public int width;
    public int height;

    @Override
    protected
    List<String> getFieldOrder() {
        return Arrays.asList("x", "y", "width", "height");
    }
}
