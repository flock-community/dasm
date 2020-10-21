package compiler.common

interface ByteConcatAble {
    val byte: Byte
}

operator fun ByteArray.plus(byteConcatAble: ByteConcatAble) = this + byteConcatAble.byte
operator fun ByteConcatAble.plus(byteArray: ByteArray) = byteArrayOf(byte) + byteArray
