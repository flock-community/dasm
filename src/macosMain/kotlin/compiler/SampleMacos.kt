package compiler

import compiler.languages.DasmLanguageSpec

fun main() {

    val source = "druk af 14;"
//    val source = "waarde getal wordt 14;"
//    val source = "waarde getal wordt 14; druk af getal;"

//    SwasmLanguageSpec.compile("print MOFO!1!! 11;") // For regression purpose

    println(DasmLanguageSpec.compile(source).toJSON())
}

fun ByteArray.toJSON() = "[${joinToString(",")}]"
