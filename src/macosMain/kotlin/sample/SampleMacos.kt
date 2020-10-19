package sample


fun main() {

    val compiled = "druk af 14;".compile()
//    val compiled = "waarde getal wordt 14;".compile()
//    val compiled = "waarde getal wordt 14; druk af getal;".compile()

    println(compiled.toJSON())
}

fun ByteArray.toJSON() = "[${joinToString(",")}]"
