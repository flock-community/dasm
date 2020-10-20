package sample

import sample.utils.log

typealias AST = List<Node>

fun compile(source:String) = source.tokenize().let { it + Token(type = EOP, value = "EOP", index = source.length + 1L) }
    .also { log("********** TOKENIZED **********\n$it\n########## TOKENIZED ##########") }
    .parse()
    .also { log("********** PARSED **********\n$it\n########## PARSED ##########") }
    .emit()
    .apply { log("********** EMITTED **********\n${joinToString(", ")}\n########## EMITTED ##########") }
