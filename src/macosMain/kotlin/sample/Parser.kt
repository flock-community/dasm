package sample

fun List<Token>.parse(): Program = filterNot { it.type is Whitespace }
