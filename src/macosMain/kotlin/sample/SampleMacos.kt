package sample

fun main() {

    val source = "druk af 14;"
//    val source = "waarde getal wordt 14;"
//    val source = "waarde getal wordt 14; druk af getal;"

    println(compile(source).toJSON())
}

fun ByteArray.toJSON() = "[${joinToString(",")}]"
