package sample

import languagespec.SwasmLanguageSpec

fun main() {

    val source = "print MOFO!1!! 14;"
//    val source = "waarde getal wordt 14;"
//    val source = "waarde getal wordt 14; druk af getal;"

    println(SwasmLanguageSpec.compile(source).toJSON())
}

fun ByteArray.toJSON() = "[${joinToString(",")}]"
