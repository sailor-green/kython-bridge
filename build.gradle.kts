plugins {
    kotlin("multiplatform") version "1.3.70-eap-184"
}

group = "green.sailor.kython"
version = "1.0-SNAPSHOT"

repositories {
    maven { setUrl("https://dl.bintray.com/kotlin/kotlin-eap") }
    mavenCentral()
    mavenLocal()
}

kotlin {

    linuxX64 {

        compilations.getByName("main") {
            sourceSets.getByName("linuxX64Main") {
                dependencies {
                    implementation("com.squareup.okio:okio-multiplatform:2.5.1-SNAPSHOT")
                }
            }

            cinterops {
                val cpython by cinterops.creating {
                    defFile(project.file("cpython.def"))
                    packageName("cpython")
                    compilerOpts("-I/usr/include/python3.9")
                }
            }
        }

        binaries {
            // executable()
            sharedLib {
                baseName = "kython-bridge"
                linkerOpts.addAll(
                    ("-L/usr/lib -L/usr/lib/python3.9/config-3.9-x86_64-linux-gnu/ " +
                    "-lcrypt -lpthread -ldl -lutil -lm -lm").split(" ")
                )
            }
        }
    }
}
