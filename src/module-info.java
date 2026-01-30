module dorkbox.jna {
    exports dorkbox.jna;
    exports dorkbox.jna.rendering;
    exports dorkbox.jna.linux;
    exports dorkbox.jna.linux.structs;
    exports dorkbox.jna.macos;
    exports dorkbox.jna.macos.cocoa;
    exports dorkbox.jna.macos.foundation;
    exports dorkbox.jna.macos.structs;
    exports dorkbox.jna.windows;
    exports dorkbox.jna.windows.structs;

    requires transitive dorkbox.updates;
    requires transitive dorkbox.os;

    requires transitive kotlin.stdlib;

    requires transitive org.slf4j;

    // these are compile only deps
    requires static com.sun.jna;
    requires static com.sun.jna.platform;
}
