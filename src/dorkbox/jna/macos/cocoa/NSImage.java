/*
 * Copyright 2018 dorkbox, llc
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
package dorkbox.jna.macos.cocoa;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.sun.jna.Pointer;

import dorkbox.jna.macos.foundation.ObjectiveC;


/**
 * https://developer.apple.com/documentation/appkit/nsimage?language=objc
 */
public
class NSImage extends NSObject {

    private static final Pointer objectClass = ObjectiveC.objc_lookUpClass("NSImage");

    private static final Pointer initWithContentsOfFile = ObjectiveC.sel_getUid("initWithContentsOfFile:");
    private static final Pointer initWithData = ObjectiveC.sel_getUid("initWithData:");

    public
    NSImage(NSData data) {
        super(ObjectiveC.class_createInstance(objectClass, 0));
        ObjectiveC.objc_msgSend(this, initWithData, data);
    }

    public
    NSImage(BufferedImage image) throws IOException {
        this(toBytes(image));
    }

    public
    NSImage(byte[] imageBytes) {
        this(new NSData(imageBytes));
    }

    public
    NSImage(final File file) {
        super(ObjectiveC.class_createInstance(objectClass, 0));
        ObjectiveC.objc_msgSend(this, initWithContentsOfFile, new NSString(file.getAbsolutePath()));
    }

    private static
    byte[] toBytes(final BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
