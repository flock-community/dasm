package compiler.exceptions

open class CompilerException(message: String) : RuntimeException(message)

class EmitterException(message: String) : CompilerException(message)

class ParserException(message: String) : CompilerException(message)

class TokenizerException(message: String) : CompilerException(message)
