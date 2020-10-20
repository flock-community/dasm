package compiler.tokenize

data class Token(
    val type: Type,
    val value: String,
    val index: Long
) {
    interface Type
}

object Assignment : Token.Type
object Number : Token.Type
object Whitespace : Token.Type
object Identifier : Token.Type
object EndOfLine : Token.Type
object EndOfProgram : Token.Type

interface Keyword : Token.Type

object Print : Keyword
object Value : Keyword
