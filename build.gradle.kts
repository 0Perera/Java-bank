plugins {
    id("java")
    application
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
repositories { mavenCentral() }
dependencies {
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
}
tasks.withType<JavaCompile> { options.encoding = "UTF-8" }
application {
    mainClass.set("Main")
    applicationDefaultJvmArgs = listOf("-Dfile.encoding=UTF-8")
}