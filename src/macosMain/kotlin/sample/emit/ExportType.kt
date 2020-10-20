package sample.emit

enum class ExportType(val byte: Byte) {
    Function(0),
    Table(1),
    Memory(2),
    Global(3);

    constructor(intCode: Int) : this(intCode.toByte())
}
