package sample


fun String.compile() = tokenize().parse().emit()

    fun String.tokenize() : List<Token> {

        val matchers = listOf(Regex("^-?[.0-9]+") to "number")

        val type = matchers.filter {
            it.first matches this
        }.map {
            it.second
        }.firstOrNull()


        return listOf(type ?: "Not found")
    }

    fun List<String>.parse() : Program {

        return this
    }

    fun Program.emit(): ByteArray {

        return this.toString().encodeToByteArray()
    }


typealias Token = String
typealias StatementNode = String
typealias Program = List<StatementNode>

