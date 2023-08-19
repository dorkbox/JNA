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

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;

@SuppressWarnings("ALL")
public
class Termios extends Structure {

    // Input flags - software input processing
    public static class Input {
        public static final int 	IGNBRK = 0x00000001; // ignore BREAK condition
        public static final int 	BRKINT = 0x00000002; // map BREAK to SIGINTR
        public static final int 	IGNPAR = 0x00000004; // ignore (discard) parity errors
        public static final int 	PARMRK = 0x00000008; // mark parity and framing errors
        public static final int 	 INPCK = 0x00000010; // enable checking of parity errors
        public static final int 	ISTRIP = 0x00000020; // strip 8th bit off chars
        public static final int      INLCR = 0x00000010; // map NL into CR
        public static final int      IGNCR = 0x00000080; // ignore CR
        public static final int      ICRNL = 0x00000100; // map CR to NL (ala CRMOD)
        public static final int       IXON = 0x00000200; // enable output flow control
        public static final int      IXOFF = 0x00000400; // enable input flow control
        public static final int      IXANY = 0x00000800; // any char will restart after stop
        public static final int    IMAXBEL = 0x00002000; // ring bell on input queue full
        public static final int      IUTF8 = 0x00004000; // (since Linux 2.6.4) (not in POSIX) Input is UTF8; this allows character-erase to be correctly performed in cooked mode.
    }

    public static class Output {
        // Output flags - software output processing
        public static final int  OPOST = 0x00000001; // enable following output processing (not set = raw output)
        public static final int  ONLCR = 0x00000002; // map NL to CR-NL (ala CRMOD)
        public static final int  OCRNL = 0x00000010; // map CR to NL on output
        public static final int  ONOCR = 0x00000020; // no CR output at column 0
        public static final int ONLRET = 0x00000040; // NL performs CR function
        public static final int  OFILL = 0x00000080; // Send fill characters for a delay, rather than using a timed delay.
        public static final int  OFDEL = 0x00020000; // Fill character is ASCII DEL (0177).  If unset, fill character is ASCII NUL ('\0').  (Not implemented on Linux.)
    }


    public static class Control {
        // Control flags - hardware control of terminal
        public static final int      CSIZE = 0000060; // character size mask
        public static final int        CS5 = 0x00000000; //   5 bits (pseudo)
        public static final int        CS6 = 0x00000100; //   6 bits
        public static final int        CS7 = 0x00000200; //   7 bits
        public static final int        CS8 = 0x00000300; //   8 bits
        public static final int     CSTOPB = 0x00000400; // send 2 stop bits
        public static final int      CREAD = 0x00000800; // enable receiver
        public static final int     PARENB = 0x00001000; // parity enable
        public static final int     PARODD = 0x00002000; // odd parity, else even
        public static final int      HUPCL = 0x00004000; // hang up on last close
        public static final int     CLOCAL = 0x00008000; // ignore modem status lines
    }


    public static class Local {
        // "Local" flags - dumping ground for other state
        // Warning: some flags in this structure begin with the letter "I" and look like they belong in the input flag.
        public static final int     ECHOKE = 0x00000001; // visual erase for line kill
        public static final int      ECHOE = 0x00000002; // visually erase chars
        public static final int      ECHOK = 0x00000004; // echo NL after line kill
        public static final int       ECHO = 0x00000008; // enable echoing
        public static final int     ECHONL = 0x00000010; // echo NL even if ECHO is off
        public static final int    ECHOPRT = 0x00000020; // visual erase mode for hardcopy
        public static final int    ECHOCTL = 0x00000040; // echo control chars as ^(Char)
        public static final int       ISIG = 0x00000080; // enable signals INTR, QUIT, [D]SUSP
        public static final int     ICANON = 0x00000100; // canonicalize input lines
        public static final int     IEXTEN = 0x00000400; // enable DISCARD and LNEXT
        public static final int    EXTPROC = 0x00000800; // external processing
        public static final int     TOSTOP = 0x00400000; // stop background jobs from output
        public static final int     FLUSHO = 0x00800000; // output being flushed (state)
        public static final int     PENDIN = 0x20000000; // XXX retype pending input (state)
        public static final int     NOFLSH = 0x80000000; // don't flush after interrupt
    }


    public static class ControlChars {
        // Special Control Characters
        //
        // the value is the index into c_cc[] character array.
        public static final int VEOF = 0;
        public static final int VEOL = 1;
        public static final int VEOL2 = 2;
        public static final int VERASE = 3;
        public static final int VWERASE = 4;
        public static final int VKILL = 5;
        public static final int VREPRINT = 6;
        public static final int VINTR = 8;
        public static final int VQUIT = 9;
        public static final int VSUSP = 10;
        public static final int VDSUSP = 11;
        public static final int VSTART = 12;
        public static final int VSTOP = 13;
        public static final int VLNEXT = 14;
        public static final int VDISCARD = 15;
        public static final int VMIN = 16;
        public static final int VTIME = 17;
    }


    public static void and(NativeLong org, int value) {
        org.setValue(org.longValue() & value);
    }

    public static void and(NativeLong org, long value) {
        org.setValue(org.longValue() & value);
    }

    public static void or(NativeLong org, long value) {
        org.setValue(org.longValue() | value);
    }

    public static void or(NativeLong org, int value) {
        org.setValue(org.longValue() | value);
    }


    public static final long TCSANOW = 0x00000000;

    /**
     * input mode flags
     */
    public NativeLong inputFlags;

    /**
     * output mode flags
     */
    public NativeLong outputFlags;

    /**
     * control mode flags
     */
    public NativeLong controlFlags;

    /**
     * local mode flags
     */
    public NativeLong localFlags;

    /**
     * control characters
     */
    public byte[] controlChars = new byte[20];

    /**
     * input speed
     */
    public NativeLong inputSpeed;

    /**
     * output speed
     */
    public NativeLong outputSpeed;

    public
    Termios() {}

    @Override
    protected
    List<String> getFieldOrder() {
        return Arrays.asList("inputFlags",
                             "outputFlags",
                             "controlFlags",
                             "localFlags",
                             "controlChars",
                             "inputSpeed",
                             "outputSpeed");
    }
}
