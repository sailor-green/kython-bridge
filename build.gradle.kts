plugins {
    kotlin("multiplatform") version "1.3.71"
}

group = "green.sailor.kython"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

val mingwPath = File(System.getenv("MINGW64_DIR") ?: "C:/msys64/mingw64")

kotlin {

    // Determine host preset.
    val hostOs = System.getProperty("os.name")

    // Create target for the host platform.
    val hostTarget = when {
        hostOs == "Mac OS X" -> macosX64("cpython")
        hostOs == "Linux" -> linuxX64("cpython")
        hostOs.startsWith("Windows") -> mingwX64("cpython")
        else -> throw GradleException("Host OS '$hostOs' is not a supported python bridge host.")
    }

    hostTarget.apply {
        binaries {
            sharedLib {
                baseName = "kython-bridge"
                val linuxArgs = "-lcrypt -lpthread -ldl -lutil -lm -lm".split(" ").toTypedArray()
                val osxArgs = "-ldl -framework CoreFoundation".split(" ").toTypedArray()

                when (preset) {
                    presets["macosX64"] -> linkerOpts(
                        "-L/usr/lib",
                        "-L/Library/Frameworks/Python.framework/Versions/3.9/lib/python3.9/config-3.9-darwin/",
                        *osxArgs
                    )
                    presets["linuxX64"] -> linkerOpts(
                        "-L/usr/lib -L/usr/lib/python3.9/config-3.9-x86_64-linux-gnu/",
                        *linuxArgs
                    )
                    else -> throw GradleException("$preset is currently not supported.")
                }

            }
        }

        compilations.getByName("main") {
            cinterops {
                val cpython by creating {
                    packageName("cpython")
                    when (preset) {
                        presets["macosX64"] -> compilerOpts("-I/Library/Frameworks/Python.framework/Versions/3.9/include/python3.9")
                        presets["linuxX64"] -> compilerOpts("-I/usr/include/python3.9")
                        else -> throw GradleException("$preset is currently not supported.")
                    }

                }
            }

            dependencies {
                implementation("com.squareup.okio:okio-multiplatform:2.5.0")
            }
        }
    }
}
