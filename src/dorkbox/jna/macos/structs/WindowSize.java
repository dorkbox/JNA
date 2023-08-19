/*
 * Copyright 2023 dorkbox, llc
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

package dorkbox.jna.macos.structs;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class WindowSize extends Structure {
    public short ws_row;
    public short ws_col;
    public short ws_xpixel;
    public short ws_ypixel;

    public WindowSize() {}

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList("ws_row",
                             "ws_col",
                             "ws_xpixel",
                             "ws_ypixel"
        );
    }
}
