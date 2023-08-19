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
package dorkbox.jna.macos;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.IntByReference;

import dorkbox.jna.macos.structs.Termios;
import dorkbox.jna.macos.structs.WindowSize;

@SuppressWarnings("ALL")
public
class CLibraryApple {
    static {
        Native.register("c");
    }

    public static NativeLong TIOCGWINSZ = new NativeLong(0x40087468L);


    public static native
    int isatty(int fd);

    public static native
    int read(int fd, IntByReference c, int count);

    /**
     * https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man2/ioctl.2.html
     * Original signature : <code>int ioctl(int fildes, unsigned long request, ...);</code><br>
     */
    public static native
    int ioctl(int fd, NativeLong request, WindowSize data);

    /**
     * https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man3/tcgetattr.3.html
     *
     * Put the state of FD into *TERMIOS_P.<br>
     * <p>
     * Original signature : <code>int tcgetattr(int fildes, struct termios *termios_p)</code><br>
     */
    public static native
    int tcgetattr(int fd, Termios termios_p);

    /**
     * https://developer.apple.com/library/archive/documentation/System/Conceptual/ManPages_iPhoneOS/man3/tcsetattr.3.html
     *
     * Set the state of FD to *TERMIOS_P.<br>
     * <p>
     * Values for OPTIONAL_ACTIONS (TCSA*) are in <bits/termios.h>.<br>
     * <p>
     * Original signature : <code>tcsetattr(int fildes, int optional_actions, const struct termios *termios_p);</code><br>
     */
    public static native
    int tcsetattr(int fd, long optional_actions, Termios termios_p);
}
