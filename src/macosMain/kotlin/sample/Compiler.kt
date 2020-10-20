package sample

import languagespec.LanguageSpec
import sample.utils.log

typealias AST = List<Node>

fun LanguageSpec.compile(source: String) = tokenize(source)
    .also { log("********** TOKENIZED **********\n$it\n########## TOKENIZED ##########") }
    .parse()
    .also { log("********** PARSED **********\n$it\n########## PARSED ##########") }
    .emit()
    .apply { log("********** EMITTED **********\n${joinToString(", ")}\n########## EMITTED ##########") }

