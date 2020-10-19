package sample

import sample.ProgramNode.PrintStatement
import sample.exceptions.EmitterException
import sample.utils.log
import sample.utils.toIEEE754Array
import sample.utils.unsignedLeb128

fun Program.emit(): ByteArray = map { it.emit() }
    .reduce { acc, cur -> acc + cur }
    .let { byteArrayOf(10, 11, 1, 9, 0) + it + 11 }
    .let { emitHeader() + emitModuleVersion() + emitTypeSection() + emitImportSection() + emitFunctionSection() + emitExportSection() + it }

fun emitHeader() = byteArrayOf(0x00, 0x61, 0x73, 0x6d)
fun emitModuleVersion() = byteArrayOf(0x01, 0x00, 0x00, 0x00)
fun emitTypeSection() = byteArrayOf(1, 8, 2, 96, 1, 125, 0, 96, 0, 0)
fun emitImportSection() = byteArrayOf(2, 27, 2, 3, 101, 110, 118, 5, 112, 114, 105, 110, 116, 0, 0, 3, 101, 110, 118, 6, 109, 101, 109, 111, 114, 121, 2, 0, 1)
fun emitFunctionSection() = byteArrayOf(3, 2, 1, 1)
fun emitExportSection() = byteArrayOf(7, 7, 1, 3, 114, 117, 110, 0, 1)

private fun ProgramNode.emit(): ByteArray {
    log("Emitting Program Node $this")
    return when (this) {
        is PrintStatement -> emitPrintStatement()
        else -> throw EmitterException("Unknown program node: $this")
    }
}

private fun PrintStatement.emitPrintStatement(): ByteArray {
    log("Emitting Print Statement")
    return expression.emit() + Opcode.call.toByte() + unsignedLeb128(0L)
}

private fun ExpressionNode.emit(): ByteArray {
    log("Emitting Expression $this")
    return when (this) {
        is ExpressionNode.NumberNode -> byteArrayOf(Opcode.f32_const.toByte()) + number.toIEEE754Array()
        else -> throw EmitterException("Unknown expression: $this")
    }
}

enum class Opcode(private val code: Int) {
    block(0x02),
    loop(0x03),
    br(0x0c),
    br_if(0x0d),
    end(0x0b),
    call(0x10),
    get_local(0x20),
    set_local(0x21),
    i32_store_8(0x3a),
    i32_const(0x41),
    f32_const(0x43),
    i32_eqz(0x45),
    i32_eq(0x46),
    f32_eq(0x5b),
    f32_lt(0x5d),
    f32_gt(0x5e),
    i32_and(0x71),
    f32_add(0x92),
    f32_sub(0x93),
    f32_mul(0x94),
    f32_div(0x95),
    i32_trunc_f32_s(0xa8);

    fun toByte() = code.toByte()

}