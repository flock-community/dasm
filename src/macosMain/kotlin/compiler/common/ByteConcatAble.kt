package compiler.common

interface ByteConcatAble {
    val byte: Byte
}

operator fun ByteArray.plus(byteConcatAble: ByteConcatAble) = this + byteConcatAble.byte
operator fun ByteConcatAble.plus(byteArray: ByteArray) = byteArrayOf(byte) + byteArray

operator fun Byte.plus(byteConcatAble: ByteConcatAble) = byteArrayOf(this) + byteConcatAble
operator fun ByteConcatAble.plus(byte: Byte) = byteArrayOf(byte) + byte
