package compiler.emit

// https://webassembly.github.io/spec/core/binary/types.html
enum class ValueType(val byte: Byte) {
    i32(0x7f),
    f32(0x7d);

    constructor(intCode: Int) : this(intCode.toByte())
}
