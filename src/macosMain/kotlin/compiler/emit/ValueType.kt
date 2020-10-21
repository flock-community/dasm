package compiler.emit

import compiler.common.ByteArrayAble
import compiler.common.ByteConcatAble
import compiler.common.EncodeAble

// https://webassembly.github.io/spec/core/binary/types.html
enum class ValueType(override val byte: Byte) : ByteConcatAble, ByteArrayAble, EncodeAble {
    i32(0x7f),
    f32(0x7d);

    constructor(intCode: Int) : this(intCode.toByte())
}
