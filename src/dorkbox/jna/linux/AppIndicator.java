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
package dorkbox.jna.linux;

import org.slf4j.LoggerFactory;

import com.sun.jna.Pointer;

import dorkbox.jna.JNA;
import dorkbox.jna.linux.structs.AppIndicatorInstanceStruct;
import dorkbox.os.OS;

/**
 * bindings for libappindicator
 *
 * Direct-mapping, See: https://github.com/java-native-access/jna/blob/master/www/DirectMapping.md
 */
@SuppressWarnings({"Duplicates", "SameParameterValue", "DanglingJavadoc"})
public
class AppIndicator {
    public static final boolean isLoaded;

    /**
     * Loader for AppIndicator, because it is absolutely mindboggling how those whom maintain the standard, can't agree to what that
     * standard library naming convention or features/API set is. We just try until we find one that works, and are able to map the
     * symbols we need. There are bash commands that will tell us the linked library name, however - I'd rather not run bash commands
     * to determine this.
     *
     * This is so hacky it makes me sick.
     */
    static {
        boolean _isLoaded = false;

        boolean shouldLoadAppIndicator = !(OS.INSTANCE.isWindows() || OS.INSTANCE.isMacOsX());
        if (!shouldLoadAppIndicator) {
            _isLoaded = true;
        }

        // GTK must be loaded!!
        if (!Gtk.isLoaded) {
            shouldLoadAppIndicator = false;
            _isLoaded = true;
        }

        // objdump -T /usr/lib/x86_64-linux-gnu/libappindicator.so.1 | grep foo
        // objdump -T /usr/lib/x86_64-linux-gnu/libappindicator3.so.1 | grep foo

        // NOTE:
        //  ALSO WHAT VERSION OF GTK to use? appindiactor1 -> GTK2, appindicator3 -> GTK3.
        //     appindiactor1 is GKT2 only (can't use GTK3 bindings with it)
        //     appindicator3 doesn't support menu icons via GTK2!!

        // appindicator3 doesn't support menu icons via GTK2!! *but it can work*  --  we ignore this use-case, because it's so buggy
        // We want to load the matching appindicator library to the loaded GTK version

        String[] GTK2 = new String[] {"appindicator", "appindicator1", "appindicator-gtk"};
        String[] GTK3 = new String[] {"appindicator3", "appindicator3-1", "appindicator-gtk3", "appindicator-gtk3-1"};

        // NOTE:  appindicator1 -> GTk2, appindicator3 -> GTK3.
        // Note: appindicator-gtk3 is Fedora...

        if (Gtk.isGtk2) {
            for (String libraryName : GTK2) {
                if (!_isLoaded) {
                    try {
                        JNA.register(libraryName, AppIndicator.class);
                        _isLoaded = true;
                        if (GtkEventDispatch.DEBUG) {
                            LoggerFactory.getLogger(AppIndicator.class).debug("Loaded GTK2 library name '{}'.", libraryName);
                        }
                    } catch (Throwable e) {
                        if (GtkEventDispatch.DEBUG) {
                            LoggerFactory.getLogger(AppIndicator.class).debug("Error loading GTK2 library name '{}'.", libraryName, e);
                        }
                    }
                }
            }
        }

        if (Gtk.isGtk3) {
            for (String libraryName : GTK3) {
                if (!_isLoaded) {
                    try {
                        JNA.register(libraryName, AppIndicator.class);
                        _isLoaded = true;
                        if (GtkEventDispatch.DEBUG) {
                            LoggerFactory.getLogger(AppIndicator.class).debug("Loaded GTK3 library name '{}'.", libraryName);
                        }
                    } catch (Throwable e) {
                        if (GtkEventDispatch.DEBUG) {
                            LoggerFactory.getLogger(AppIndicator.class).debug("Error loading GTK3 library name '{}'.", libraryName, e);
                        }
                    }
                }
            }
        }

        // We can fall back to GtkStatusIndicator or Swing if this cannot load
        isLoaded = shouldLoadAppIndicator && _isLoaded;
    }

    // Note: AppIndicators DO NOT support tooltips, as per mark shuttleworth. Rather stupid IMHO.
    // See: https://bugs.launchpad.net/indicator-application/+bug/527458/comments/12


    public static
    String getInstallString(boolean isGtk2) {
        // ARCH
        // requires the install of libappindicator which is GTK2 (as of 25DEC2016)
        // requires the install of libappindicator3 which is GTK3 (as of 25DEC2016)

        // FEDORA
        // libappindicator-gtk
        // libappindicator-gtk3

        // ARCH
        // libappindicator-gtk2
        // libappindicator-gtk3

        // Debian based
        // libappindicator
        // libappindicator3 (or 3-1)


        if (isGtk2) {
            if (OS.Linux.INSTANCE.isDebian()) {
                // debian is slightly different
                return "libappindicator1";
            }
            else if (OS.Linux.INSTANCE.isFedora()) {
                return "libappindicator-gtk";
            }
            else if (OS.Linux.INSTANCE.isArch()) {
                return "libappindicator-gtk2";
            }
            else {
                return "libappindicator";
            }
        } else {
            if (OS.Linux.INSTANCE.isDebian()) {
                // debian is slightly different
                return "libappindicator3-1";
            }
            else if (OS.Linux.INSTANCE.isFedora()) {
                return "libappindicator-gtk3";
            }
            else if (OS.Linux.INSTANCE.isArch()) {
                return "libappindicator-gtk3";
            }
            else {
                return "libappindicator3";
            }
        }
    }

    public static final int CATEGORY_APPLICATION_STATUS = 0;
//    public static final int CATEGORY_COMMUNICATIONS = 1;
//    public static final int CATEGORY_SYSTEM_SERVICES = 2;
//    public static final int CATEGORY_HARDWARE = 3;
//    public static final int CATEGORY_OTHER = 4;

    public static final int STATUS_PASSIVE = 0;
    public static final int STATUS_ACTIVE = 1;
//    public static final int STATUS_ATTENTION = 2;


    public static native
    AppIndicatorInstanceStruct app_indicator_new(String id, String icon_name, int category);

    public static native void app_indicator_set_title(Pointer indicator, String title);
    public static native void app_indicator_set_status(Pointer indicator, int status);
    public static native void app_indicator_set_menu(Pointer indicator, Pointer menu);
    public static native void app_indicator_set_icon(Pointer indicator, String icon_name);
}
