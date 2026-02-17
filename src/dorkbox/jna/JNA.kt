/*
 * Copyright 2026 dorkbox, llc
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
package dorkbox.jna

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.NativeLibrary
import com.sun.jna.win32.W32APIOptions
import dorkbox.os.OS.isWindows
import dorkbox.updates.Updates.add

/**
 * Helper method to get the library info from JNA when registering via direct map
 */
object JNA {
    /**
     * Gets the version number.
     */
    const val version = "1.7"

    init {
        // Add this project to the updates system, which verifies this class + UUID + version information
        add(JNA::class.java, "b416694a22ed4a1d9e612a1e9b6c76cf", version)
    }

    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun register(libraryName: String?, clazz: Class<*>): NativeLibrary {
        val options = mutableMapOf<String?, Any?>()
        options[Library.OPTION_CLASSLOADER] = clazz.classLoader

        if (isWindows) {
            val entries = W32APIOptions.DEFAULT_OPTIONS.entries
            for (entry in entries) {
                options[entry.key] = entry.value
            }
        }

        val library = NativeLibrary.getInstance(libraryName, options)
        requireNotNull(library) { "$libraryName doesn't exist or cannot be loaded." }

        Native.register(clazz, library)
        return library
    }
}
