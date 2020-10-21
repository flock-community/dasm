package compiler.emit

import compiler.common.ByteArrayAble
import compiler.common.ByteConcatAble

// https://webassembly.github.io/spec/core/binary/modules.html#sections
enum class Section(override val byte: Byte) : ByteConcatAble, ByteArrayAble {
    Custom(0),
    Type(1),
    Import(2),
    Function(3),
    Table(4),
    Memory(5),
    Global(6),
    Export(7),
    Start(8),
    Element(9),
    Code(10);
    //Data(11);

    constructor(intCode: Int) : this(intCode.toByte())
}
