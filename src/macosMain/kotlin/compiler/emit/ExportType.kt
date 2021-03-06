package compiler.emit

import compiler.common.ByteArrayAble
import compiler.common.ByteConcatAble

enum class ExportType(override val byte: Byte) : ByteConcatAble, ByteArrayAble {
    Function(0),
    Table(1),
    Memory(2),
    Global(3);

    constructor(intCode: Int) : this(intCode.toByte())
}
