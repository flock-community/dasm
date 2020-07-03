package sample

fun String.compile() = tokenize().parse().transform().emit()



