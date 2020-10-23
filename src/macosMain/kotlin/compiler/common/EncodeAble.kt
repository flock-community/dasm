package compiler.common

import compiler.emit.unsignedLeb128

interface EncodeAble : ByteArrayAble

fun EncodeAble.encode() = encodeVector(toByteArray())
fun Int.encode() = encodeVector(byteArrayOf(toByte()))
fun ByteArray.encode() = encodeVector(this)
fun List<ByteArray>.encode() = encodeVector(*toTypedArray(), useSizeOne = true)

private fun encodeVector(vararg data: ByteArray, useSizeOne: Boolean = false) = data
    .let { if (it.size == 1 && !useSizeOne) it[0].size to it[0] else it.size to it.fold(byteArrayOf()) { acc, bytes -> acc + bytes } }
    .let { (size, data) -> byteArrayOf(unsignedLeb128(size)) + data }


fun String.encode() = byteArrayOf(length.toByte()) + map { it.toByte() }