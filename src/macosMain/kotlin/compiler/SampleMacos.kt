package compiler

import compiler.languages.DasmLanguageSpec
import compiler.languages.SwasmLanguageSpec
import kotlinx.cinterop.toKString
import platform.posix.getenv

fun main() {

//    val source = "druk af 14;"
//    val source = "waarde getal wordt 14;"
//    val source = "druk af 14; waarde getal wordt 12;"
//    val source = "waarde getal wordt 14; druk af getal;"
    val source = "druk af 14; waarde getal wordt 12; druk af getal;"

    SwasmLanguageSpec.compile("print MOFO!1!! 11;") // For regression purpose
        .also { println(it.toJSON()) }

    DasmLanguageSpec.compile(source)
        .also { println(it.toJSON()) }
}

fun ByteArray.toJSON() = "[${joinToString(",")}]"
