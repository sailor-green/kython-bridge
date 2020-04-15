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
                // Not sure what to put here yet for platforms other than linux.
                linkerOpts.addAll(
                    ("-L/usr/lib -L/usr/lib/python3.9/config-3.9-x86_64-linux-gnu/ " +
                        "-lcrypt -lpthread -ldl -lutil -lm -lm").split(" ")
                )
            }
        }

        compilations.getByName("main") {
            cinterops {
                val cpython by creating {
                    when (preset) {
                        presets["macosX64"] -> includeDirs.headerFilterOnly(
                            "/usr/local/include"
                        )
                        presets["linuxX64"] -> includeDirs.headerFilterOnly(
                            "/usr/include",
                            "/usr/include/x86_64-linux-gnu"
                        )
                        presets["mingwX64"] -> includeDirs.headerFilterOnly(mingwPath.resolve("include"))
                    }
                }
            }

            dependencies {
                implementation("com.squareup.okio:okio-multiplatform:2.5.0")
            }
        }
    }
}
