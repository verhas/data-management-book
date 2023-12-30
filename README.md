# Data Management Book

Manuscript and support files for the book about Data Management.

## Building the book

The text of the book is edited in ASCIIDOC format using the Jamal preprocessor.
The source files are in the directory `docs`.
Jamal converts `.adoc.jam` files to `.adoc` files.
The Ruby script `convert` converts the `.adoc` files `.fodt`.

The conversion is using the packt style imported from the github repository:

```text
https://github.com/gregturn/asciidoctor-packt
```

Text manuscript follows the one sentence one line principle.
Help to write short and coincide sentences easy to read.

The diagrams are edited using Omnigraffle.
They are in the directory  `images`.
The AppleScript `onmi2svg.scpt` converts these files to SVG and PNG format.
Every chapter has its own directory for the `.graffle` files.
Every `.graffle` file starts with the `chNN_` prefix where

* `ch` is a literal, abbreviation standing for chapter
* `NN` is `01`, `02` .. `11` is the chapter serial number

The script `./build` (Ruby) executes all the commands that are needed to create the final files.
It does the following actions:

* converts the `.graffle` files to `.svg`,

* creates the `.svg.jim` files that define the side of the SVG files for ASCIIDOC image inclusion

* generates the `pom.xml` files from `pom.xml.jam`,

* compiles the Java source,

* runs the tests, which generate output to be included into the book text,

* processes the text files with Jamal,

* converts `.adoc` files `.fodt`.

To run the applications Java 16 loom version has to be used.
The current setup is:

```text
$ java -version
openjdk version "16-loom" 2021-03-16
OpenJDK Runtime Environment (build 16-loom+6-105)
OpenJDK 64-Bit Server VM (build 16-loom+6-105, mixed mode, sharing)
```

When the application unit tests are executing then some of the unit tests call the `main()` of some classes redirecting the output to sample files.
These files will be created in `generated/output`.
The name of the file will be the full name of the class containing the `main()`.
The redirection also prepends the text `// snippet fileName` at the start of the file.
At the end of the file there will be an `// end snippet`. For example the file `generated/output/javax0.loombook.snippets.TestHelloWorld.txt` contains

```text
// snippet javax0_loombook_snippets_TestHelloWorld_
Hello, World.
// end snippet
```
(This is copied here, may not be up to date, but for the documentation purpose it is okay.
Also a trailing _ was added to the snippet name not to confuse pyama.)

## Cleaning

The script `./clean` (Python) script deletes the generated files.

## Committing to Git

The generated files are not committed to github.
The exceptions are:

* `pom.xml` files, in case someone wants to checkout the code and compileusing only the standard tools.
Note that Jamal is not a standard tool.

* `README.md` because it is displayed on the opening page by GitHub.

* `generated/images` files as some of them is displayed online and they are also needed by the editors.

## Files and Directories

* `code` contains the Java project files. This is the root directory of the multi-module maven parent project.

  * `genpom.xml` is the POM file configuring Jamal maven plugin.
    Use it `mvn -f genpom.xml clean` to convert `pom.xml.jam` -> `pom.xml`

  * `pom.jim` contains the macros imported by in `pom.xml.jam`

  * `plugins.jim` is included by `pom.xml.jam` and contains the plugins.
    This is a common part for all subprojects POMs therefore moved to a separate file.

  * `version.jim` macro file defining the versions

* `docs` contains the chapters

* `images` contains the illustrations

* `generated` contains generated files

* `generated/output` contains the output from the tests

* `generated/images` contains the images converted from Omnigraffle to PNG and SVG

* `pyama` contains the Python source code for Pyama, slightly modified for the project

* `build` is a Python script that runs all tasks to build the book

* `clean` is a Python script that deletes all intermediary files

* `jamal.options` contains the options on how Jamal should run

* `omni2svg.scpt` is an apple script that exports SVG and PNG files from an Omnigraffle file

* `svg2size` reads the `.svg` files in the directory `generated/images` and outputs `.svg.jim` files.
   These files contain a Jamal macro definition `picsize` that can be used in asciidoc to size the image.

* `pyama.py` is the startup and configuration script for pyama to copy the snippets into the book text source

*  `ROOT` is a placeholder file.
   Its content is not interesting.
   It has to be in the root directory of the project.
   Code sample output redirection uses this file to identify which is the project root directory, so that it knows where to put the output snippets.

* `README.md` is a generated file

* `README.md.jam` is the source code for `README.md` (`README.md.jam` -> `README.md`)

* `Scalable Concurrency with Project Loom.docx` word document with the original planned structure

* `Scalable Concurrency with Project Loom-vp-1.0.0.docx` is the version accepted by Packt.