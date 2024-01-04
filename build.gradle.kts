import javax0.jamal.api.Position
import javax0.jamal.engine.Processor
import javax0.jamal.tools.Input
import javax.xml.parsers.DocumentBuilderFactory

buildscript {
    repositories {
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }
    dependencies {
        classpath("com.javax0.jamal:jamal-all:2.5.0")
    }
}

tasks.register("graffleopen") {
    doLast {
        layout.projectDirectory
            .dir("images/")
            .asFile
            .walkTopDown()
            .filter { ".*\\.graffle".toRegex().containsMatchIn(it.name) }
            .forEach {
                println("opening ${it.parentFile}/${it.name}")
                exec {
                    workingDir = it.parentFile
                    commandLine = listOf("open", it.name)
                }
            }
    }
}

tasks.register("graffleconvert") {
    dependsOn("graffleopen")
    doLast {
        layout.projectDirectory
            .dir("images/")
            .asFile
            .walkTopDown()
            .filter { ".*\\.graffle".toRegex().containsMatchIn(it.name) }
            .forEach {
                exec {
                    workingDir = it.parentFile
                    commandLine = listOf("osascript", "../omni2svg.scpt", it.name.replace(".graffle", ""))
                }
            }
    }
}

tasks.register("svgsize") {
    dependsOn("graffleconvert")
    doLast {
        layout.projectDirectory
            .dir("generated/images/")
            .asFile
            .walkTopDown()
            .filter { ".*\\.svg$".toRegex().containsMatchIn(it.name) }
            .forEach {
                val docFactory = DocumentBuilderFactory.newInstance()
                val docBuilder = docFactory.newDocumentBuilder()
                val doc = docBuilder.parse(it)

                val width = doc.getElementsByTagName("svg").item(0).attributes.getNamedItem("width").textContent
                val height = doc.getElementsByTagName("svg").item(0).attributes.getNamedItem("height").textContent

                val content = """
                ------------------------------------------------------------------------
                {%@comment
                use it in the .adoc.jam file as

                {%@import ../${it.nameWithoutExtension}.svg.jim%}

                image::../${it.name}[{%picsize%}]

                NOTE: there is an empty line after the import
                %}
                ------------------------------------------------------------------------

                {%@define picsize=width="${width}px" height="${height}px"%}
            """.trimIndent()

                File("${it.path}.jim").writeText(content)

            }
    }
}

tasks.register("jamal") {
    dependsOn("svgsize")
    doLast {
        layout.projectDirectory
            .dir("docs/")
            .asFile
            .walkTopDown()
            .filter { ".*\\.adoc\\.jam".toRegex().containsMatchIn(it.name) }
            .forEach {
                val output = layout.projectDirectory.file("docs/${it.name.replace(".jam", "")}").asFile
                if (!output.exists() || (output.exists() && (output.lastModified() <= it.lastModified()))) {
                    val processor = Processor("{%", "%}")
                    val input = Input.makeInput(it.readText(Charsets.UTF_8), Position(it.absolutePath, 0, 0))
                    println("converting ${it.absolutePath} to ${output.absolutePath}")
                    val result = processor.process(input)
                    output.writeText(result)
                } else {
                    println("skipping ${it.absolutePath} to ${output.absolutePath}")
                }
            }

        println("Jamal conversion done")
    }
}

tasks.register("todoc") {
    dependsOn("jamal")
    doLast {
        layout.projectDirectory
            .dir("docs/")
            .asFile
            .walkTopDown()
            .filter { ".*\\.adoc$".toRegex().containsMatchIn(it.name) }
            .forEach {
                exec {
                    workingDir = it.parentFile
                    commandLine = listOf("asciidoctor", "-T", "../slim", "-b", "book", "$it", "-o", "${it.nameWithoutExtension}.fodt")
                }
            }
    }
}

