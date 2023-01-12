/*
 * Copyright 2015 dorkbox, llc
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

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

public
class GdkEventButton extends Structure {
    public int type;
    public Pointer window;
    public int send_event;
    public int time;
    public double x;
    public double y;
    public Pointer axes;
    public int state;
    public int button;
    public Pointer device;
    public double x_root;
    public double y_root;

    @Override
    protected
    List<String> getFieldOrder() {
        return Arrays.asList("type", "window", "send_event", "time", "x", "y", "axes", "state", "button", "device", "x_root", "y_root");
    }
}
