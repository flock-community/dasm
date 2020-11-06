package compiler

import compiler.emit.emit
import compiler.languages.LanguageSpec
import compiler.parse.Node
import compiler.parse.parse
import compiler.tokenize.tokenize
import compiler.utils.log

typealias AST = List<Node>

fun LanguageSpec.compile(source: String) = tokenize(source)
    .also { log("********** TOKENIZED **********\n$it\n########## TOKENIZED ##########") }
    .parse()
    .also { log("********** PARSED **********\n$it\n########## PARSED ##########") }
    .emit()
    .apply { log("********** EMITTED **********\n${joinToString(", ")}\n########## EMITTED ##########") }
