package sample.utils

/**
 * Reads an unsigned integer from byteBuffer.
 */
fun unsignedLeb128(long: Long) = long.toUnsignedLeb128()

fun Long.toUnsignedLeb128(): Byte {
    var result = 0
    var current: Int
    var count = 0

    do {
        current = toInt() and 0xff
        result = result or (current and 0x7f shl count * 7)
        count++
    } while (current and 0x80 == 0x80 && count < 5)

    if (current and 0x80 == 0x80) {
        throw NumberFormatException("invalid LEB128 sequence")
    }

    return result.toByte()
}

fun Float.toIEEE754Array(): ByteArray = toRawBits()
    .let {
        byteArrayOf(
            it.toByte(),
            (it shr 8).toByte(),
            (it shr 16).toByte(),
            (it shr 24).toByte()
        )
    }

