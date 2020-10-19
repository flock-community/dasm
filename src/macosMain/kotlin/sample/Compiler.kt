package sample

fun String.compile() = tokenize().let { it + Token(type = EOP, value = "EOP", index = length + 1L) }
    .also { println("********** TOKENIZED **********\n$it\n########## TOKENIZED ##########") }
    .parse()
    .also { println("********** PARSED **********\n$it\n########## PARSED ##########") }
    .emit()
    .apply { println("********** EMITTED **********\n${joinToString(", ")}\n########## EMITTED ##########") }

typealias Program = List<ProgramNode>
