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

///////////////////////////////
//////    PUBLISH TO SONATYPE / MAVEN CENTRAL
////// TESTING : (to local maven repo) <'publish and release' - 'publishToMavenLocal'>
////// RELEASE : (to sonatype/maven central), <'publish and release' - 'publishToSonatypeAndRelease'>
///////////////////////////////

gradle.startParameter.showStacktrace = ShowStacktrace.ALWAYS   // always show the stacktrace!

plugins {
    id("com.dorkbox.GradleUtils") version "3.18"
    id("com.dorkbox.Licensing") version "2.24"
    id("com.dorkbox.VersionUpdate") version "2.8"
    id("com.dorkbox.GradlePublish") version "1.22"

    kotlin("jvm") version "1.9.0"
}

object Extras {
    // set for the project
    const val description = "Native JNA extensions for Linux, MacOS, and Windows, Java 1.8+"
    const val group = "com.dorkbox"
    const val version = "1.4"

    // set as project.ext
    const val name = "JNA"
    const val id = "JNA"
    const val vendor = "Dorkbox LLC"
    const val vendorUrl = "https://dorkbox.com"
    const val url = "https://git.dorkbox.com/dorkbox/JNA"
}

///////////////////////////////
/////  assign 'Extras'
///////////////////////////////
GradleUtils.load("$projectDir/../../gradle.properties", Extras)
GradleUtils.defaults()
GradleUtils.compileConfiguration(JavaVersion.VERSION_1_8)
GradleUtils.jpms(JavaVersion.VERSION_1_9)

licensing {
    license(License.APACHE_2) {
        description(Extras.description)
        author(Extras.vendor)
        url(Extras.url)
    }
}

tasks.jar.get().apply {
    manifest {
        // https://docs.oracle.com/javase/tutorial/deployment/jar/packageman.html
        attributes["Name"] = Extras.name

        attributes["Specification-Title"] = Extras.name
        attributes["Specification-Version"] = Extras.version
        attributes["Specification-Vendor"] = Extras.vendor

        attributes["Implementation-Title"] = "${Extras.group}.${Extras.id}"
        attributes["Implementation-Version"] = GradleUtils.now()
        attributes["Implementation-Vendor"] = Extras.vendor
    }
}

// NOTE: compileOnly is used because there are some classes/dependencies that ARE NOT necessary to be included, UNLESS the user
//  is actually using that part of the library. If this happens, they will (or should) already be using the dependency)
dependencies {
    api("com.dorkbox:OS:1.11")
    api("com.dorkbox:Updates:1.1")

    implementation("org.slf4j:slf4j-api:2.0.7")

    val jnaVersion = "5.13.0"
    compileOnly("net.java.dev.jna:jna-jpms:$jnaVersion")
    compileOnly("net.java.dev.jna:jna-platform-jpms:$jnaVersion")


    testImplementation("junit:junit:4.13.2")
    testImplementation("ch.qos.logback:logback-classic:1.4.5")
}


publishToSonatype {
    groupId = Extras.group
    artifactId = Extras.id
    version = Extras.version

    name = Extras.name
    description = Extras.description
    url = Extras.url

    vendor = Extras.vendor
    vendorUrl = Extras.vendorUrl

    issueManagement {
        url = "${Extras.url}/issues"
        nickname = "Gitea Issues"
    }

    developer {
        id = "dorkbox"
        name = Extras.vendor
        email = "email@dorkbox.com"
    }
}
