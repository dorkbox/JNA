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

gradle.startParameter.showStacktrace = ShowStacktrace.ALWAYS   // always show the stacktrace!
gradle.startParameter.warningMode = WarningMode.All

plugins {
    id("com.dorkbox.GradleUtils") version "4.8"
    id("com.dorkbox.Licensing") version "3.1"
    id("com.dorkbox.VersionUpdate") version "3.2"
    id("com.dorkbox.GradlePublish") version "2.2"

    kotlin("jvm") version "2.3.0"
}

GradleUtils.load {
    group = "com.dorkbox"
    id = "JNA"

    description = "Native JNA extensions for Linux, MacOS, and Windows"
    name = "JNA"
    version = "1.5"

    vendor = "Dorkbox LLC"
    vendorUrl = "https://dorkbox.com"

    url = "https://git.dorkbox.com/dorkbox/JNA"

    issueManagement {
        url = "${url}/issues"
        nickname = "Gitea Issues"
    }

    developer {
        id = "dorkbox"
        name = vendor
        email = "email@dorkbox.com"
    }
}
GradleUtils.defaults()
GradleUtils.compileConfiguration(JavaVersion.VERSION_25)


licensing {
    license(License.APACHE_2) {
        description(Extras.description)
        author(Extras.vendor)
        url(Extras.url)
    }
}


// NOTE: compileOnly is used because there are some classes/dependencies that ARE NOT necessary to be included, UNLESS the user
//  is actually using that part of the library. If this happens, they will (or should) already be using the dependency)
dependencies {
    api("com.dorkbox:OS:2.0")
    api("com.dorkbox:Updates:1.3")

    implementation("org.slf4j:slf4j-api:2.0.17")

    val jnaVersion = "5.18.1"
    compileOnly("net.java.dev.jna:jna-jpms:$jnaVersion")
    compileOnly("net.java.dev.jna:jna-platform-jpms:$jnaVersion")

    // used for better cross-module reflection access to classes normally not accessible.
    compileOnly("org.javassist:javassist:3.29.2-GA")

    testImplementation("junit:junit:4.13.2")
    testImplementation("ch.qos.logback:logback-classic:1.5.26")
}
