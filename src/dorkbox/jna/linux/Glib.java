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
package dorkbox.jna.linux;

import org.slf4j.LoggerFactory;

import com.sun.jna.Callback;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;

import dorkbox.jna.JNA;

/**
 * bindings for glib-2.0
 *
 * Direct-mapping, See: https://github.com/java-native-access/jna/blob/master/www/DirectMapping.md
 */
public
class Glib {
    static {
        try {
            NativeLibrary library = JNA.register("glib-2.0", Glib.class);
            if (library == null) {
                LoggerFactory.getLogger(Glib.class).error("Error loading Glib library, it failed to load.");
            }
        } catch (Throwable e) {
            LoggerFactory.getLogger(Glib.class).error("Error loading Glib library, it failed to load {}", e.getMessage());
        }
    }

    public interface GLogLevelFlags {
        public static final int RECURSION          = 1 << 0;
        public static final int FATAL              = 1 << 1;
        /* GLib log levels */
        public static final int ERROR             = 1 << 2;       /* always fatal */
        public static final int CRITICAL          = 1 << 3;
        public static final int WARNING           = 1 << 4;
        public static final int MESSAGE           = 1 << 5;
        public static final int INFO              = 1 << 6;
        public static final int DEBUG            = 1 << 7;
        public static final int MASK              = ~(RECURSION | FATAL);
    }

    public interface GLogFunc extends Callback {
        void callback (String log_domain, int log_level, String message, Pointer data);
    }

    public static final Glib.GLogFunc nullLogFunc = new Glib.GLogFunc() {
        @Override
        public
        void callback(final String log_domain, final int log_level, final String message, final Pointer data) {
            // do nothing
        }
    };

    public static native int g_log_set_handler(String log_domain, int levels, GLogFunc handler, Pointer user_data);
    public static native void g_log_default_handler (String log_domain, int log_level, String message, Pointer unused_data);
    public static native GLogFunc g_log_set_default_handler(GLogFunc log_func, Pointer user_data);
    public static native void g_log_remove_handler (String log_domain, int handler_id);
}
