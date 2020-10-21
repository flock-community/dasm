package compiler.common

interface ByteArrayAble {
    val byte: Byte
}

fun ByteArrayAble.toByteArray(): ByteArray = byteArrayOf(byte)
