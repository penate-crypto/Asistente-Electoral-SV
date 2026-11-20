// Prueba del motor de búsqueda REAL de la app (archivos Kotlin de app/src/main/java)
// ejecutado en la JVM, sin Android, contra el banco de conocimientos JSON.
// Uso (desde esta carpeta):  gradle -q run --args="preguntas/01_regresion.txt"
plugins {
    kotlin("jvm") version "2.2.10"
    application
}

repositories { mavenCentral() }

dependencies { implementation("org.json:json:20240303") }

kotlin { jvmToolchain(21) }

sourceSets {
    main {
        kotlin {
            srcDir("../../../app/src/main/java")
            include("com/example/data/search/**", "com/example/data/ElectoralPdfData.kt", "Main.kt", "CodeMain.kt")
        }
    }
}

application { mainClass.set(System.getenv("MAINCLS") ?: "MainKt") }

tasks.named<JavaExec>("run") {
    workingDir = projectDir
    jvmArgs("-Dstdout.encoding=UTF-8")
}
