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

internal object ClassLoaderAccessory {
    // this class is to be overridden. This is on purpose, since this allows us to SKIP reflection when accessing classes, as we can
    // redefine this class at runtime (and at compile time, it is accessible)
    internal fun findLoadedClass(classLoader: ClassLoader, className: String): Class<*>? {
        return null
    }
}
