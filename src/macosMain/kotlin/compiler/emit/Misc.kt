package compiler.emit

import compiler.common.ByteArrayAble
import compiler.common.ByteConcatAble

enum class Misc(override val byte: Byte) : ByteConcatAble, ByteArrayAble {
    FunctionType(0x60),
    EmptyArray(0x00);

    constructor(intCode: Int) : this(intCode.toByte())
}
