package compiler.emit

import compiler.AST
import compiler.common.encode
import compiler.common.plus
import compiler.emit.Misc.EmptyArray
import compiler.emit.Misc.FunctionType
import compiler.exceptions.EmitterException
import compiler.parse.ExpressionNode
import compiler.parse.ExpressionNode.IdentifierNode
import compiler.parse.ExpressionNode.NumberNode
import compiler.parse.Node
import compiler.parse.ProgramNode.PrintStatement
import compiler.parse.ProgramNode.VariableAndAssignmentDeclaration
import compiler.utils.log

fun AST.emit(): ByteArray = createHeader() +
        createModuleVersion() +
        createTypeSection() +
        createImportSection() +
        createFunctionSection() +
        createExportSection() +
        createCodeSection()

private val functions: List<String> = listOf("main")

private fun createHeader() = byteArrayOf(0x00, 0x61, 0x73, 0x6d)
private fun createModuleVersion() = byteArrayOf(1, 0, 0, 0)
private fun createTypeSection() = Create.section(Section.Type, listOf(Create.printFunctionType(), Create.functionType()).encode())
private fun createImportSection() = Create.section(Section.Import, listOf(Create.printFunctionImport(), Create.memoryImport()).encode())
private fun createFunctionSection() = Create.section(Section.Function, functions.size.encode()) // 1 because we assume (for now) that we have 1 function
private fun createExportSection() = Create.section(Section.Export, listOf(Create.runExportType()).encode())
private fun AST.createCodeSection() = emitCode() // Order matters! We need to fill the identifiers before we can extract the locals.
    .let { Create.section(Section.Code, listOf((emitLocals() + it + Opcode.end).encode()).encode()) }

private fun emitLocals(): ByteArray = identifiers
    .run { if (isEmpty()) listOf() else listOf(unsignedLeb128(size) + ValueType.f32) }
    .encode()

private fun AST.emitCode() = map { it.emit() }.reduce { acc, cur -> acc + cur }

private object Create {
    fun section(section: Section, data: ByteArray) = section + data.encode()

    fun printFunctionImport() = "env".encode() +
            "print".encode() +
            ExportType.Function +
            0x00

    fun memoryImport() = "env".encode() +
            "memory".encode() +
            ExportType.Memory +
            /* limits https://webassembly.github.io/spec/core/binary/types.html#limits -indicates a min memory size of one page */
            0x00 +
            0x01

    fun printFunctionType() = FunctionType +
            ValueType.f32.encode() +
            EmptyArray

    fun functionType() = FunctionType +
            byteArrayOf().encode() +
            EmptyArray

    fun runExportType() = "run".encode() +
            ExportType.Function +
            1.toByte() // 1 because we assume (for now) that we have 1 callable function
}

fun Node.emit(): ByteArray = also { log("Emitting Program Node $it") }
    .run {
        when (this) {
            is PrintStatement -> emitPrintStatement()
            is VariableAndAssignmentDeclaration -> emitVariableAndAssignmentDeclaration()
            else -> throw EmitterException("Unknown program node: $this")
        }
    }

private fun PrintStatement.emitPrintStatement(): ByteArray = also { log("Emitting Print Statement") }
    .let { expression.emit() + Opcode.call + unsignedLeb128(0) }

private fun VariableAndAssignmentDeclaration.emitVariableAndAssignmentDeclaration(): ByteArray = also { log("Emitting Variable And Assignment Declaration") }
    .let { numberNode.emit() + identifierNode.set().emit() }

private fun ExpressionNode.emit(): ByteArray = also { log("Emitting Expression $it") }
    .let {
        when (this) {
            is NumberNode -> Opcode.f32_const + number.toIEEE754Array()
            is IdentifierNode -> when (set) {
                true -> Opcode.set_local + byteArrayOf(unsignedLeb128(getIdentifier(value)))
                false -> Opcode.get_local + byteArrayOf(unsignedLeb128(getIdentifier(value)))
            }
            else -> throw EmitterException("Unknown expression: $this")
        }
    }

private val identifiers: MutableList<String> = mutableListOf()

private fun getIdentifier(identifierNodeValue: String): Int = with(identifiers) {
    if (!contains(identifierNodeValue)) add(identifierNodeValue)
    indexOf(identifierNodeValue)
}
