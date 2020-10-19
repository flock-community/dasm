package sample

import sample.utils.log

fun String.compile() = tokenize().let { it + Token(type = EOP, value = "EOP", index = length + 1L) }
    .also { log("********** TOKENIZED **********\n$it\n########## TOKENIZED ##########") }
    .parse()
    .also { log("********** PARSED **********\n$it\n########## PARSED ##########") }
    .emit()
    .apply { log("********** EMITTED **********\n${joinToString(", ")}\n########## EMITTED ##########") }

typealias Program = List<ProgramNode>
