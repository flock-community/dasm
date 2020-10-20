package sample.emit

import sample.AST
import sample.ExpressionNode
import sample.Node
import sample.ProgramNode.PrintStatement
import sample.exceptions.EmitterException
import sample.utils.log

fun createHeader() = byteArrayOf(0x00, 0x61, 0x73, 0x6d)
fun createModuleVersion() = byteArrayOf(1, 0, 0, 0)
fun createTypeSection() = Create.section(Section.Type, encodeVector(Create.printFuncType(), Create.funcType()))
fun createImportSection() = Create.section(Section.Import, encodeVector(Create.printFunctionImport(), Create.memoryImport()))
fun createFunctionSection() = Create.section(Section.Function, encodeVector(byteArrayOf(1.toByte()))) // 1 because we assume (for now) that we have 1 function
fun createExportSection() = Create.section(Section.Export, encodeVector(Create.runExportType(), sizeOfByteArray = false))
fun AST.createCodeSection() = Create.section(Section.Code, byteArrayOf(1, 9, 0) + map { it.emit() }.reduce { acc, cur -> acc + cur } + 11)

private fun encodeVector(vararg data: ByteArray, sizeOfByteArray: Boolean = true) = data
    .let { if (it.size == 1 && sizeOfByteArray) it[0].size to it[0] else it.size to it.reduce { acc, bytes -> acc + bytes } }
    .let { (size, data) -> byteArrayOf(unsignedLeb128(size.toLong())) + data }

private object Create {
    fun section(section: Section, data: ByteArray) = byteArrayOf(section.byte) + encodeVector(data)

    fun printFunctionImport() = "env".encode() +
            "print".encode() +
            ExportType.Function.byte +
            0x00

    fun memoryImport() = "env".encode() +
            "memory".encode() +
            ExportType.Memory.byte +
            /* limits https://webassembly.github.io/spec/core/binary/types.html#limits -indicates a min memory size of one page */
            0x00 +
            0x01

    fun printFuncType() = byteArrayOf(functionType.toByte()) +
            encodeVector(byteArrayOf(ValueType.f32.byte)) +
            emptyArray.toByte()

    fun funcType() = byteArrayOf(functionType.toByte()) +
            encodeVector(byteArrayOf()) +
            emptyArray.toByte()

    fun runExportType() = "run".encode() +
            ExportType.Function.byte +
            1.toByte() // 1 because we assume (for now) that we have 1 callable function
}

fun Node.emit(): ByteArray {
    log("Emitting Program Node $this")
    return when (this) {
        is PrintStatement -> emitPrintStatement()
        else -> throw EmitterException("Unknown program node: $this")
    }
}

private fun PrintStatement.emitPrintStatement(): ByteArray {
    log("Emitting Print Statement")
    return expression.emit() + Opcode.call.byte + unsignedLeb128(0L)
}

private fun ExpressionNode.emit(): ByteArray {
    log("Emitting Expression $this")
    return when (this) {
        is ExpressionNode.NumberNode -> byteArrayOf(Opcode.f32_const.byte) + number.toIEEE754Array()
        else -> throw EmitterException("Unknown expression: $this")
    }
}

const val functionType = 0x60
const val emptyArray = 0x00
