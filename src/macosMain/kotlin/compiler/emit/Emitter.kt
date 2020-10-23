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
import compiler.parse.ProgramNode.Print
import compiler.parse.ProgramNode.VariableAndAssignmentDeclaration
import compiler.utils.log

fun AST.emit(): ByteArray = createHeader() +
        createModuleVersion() +
        createTypeSection() +
        createImportSection() +
        createFunctionSection() +
        createExportSection() +
        createCodeSection()

private fun createHeader() = byteArrayOf(0x00, 0x61, 0x73, 0x6d)
private fun createModuleVersion() = byteArrayOf(1, 0, 0, 0)
private fun createTypeSection() = Create.section(Section.Type, listOf(Create.printFunctionType(), Create.functionType()).encode())
private fun createImportSection() = Create.section(Section.Import, listOf(Create.printFunctionImport()).encode())
private fun createFunctionSection() = Create.section(Section.Function, Create.functions.size.encode())
private fun createExportSection() = Create.section(Section.Export, listOf(Create.runExportType()).encode())
private fun AST.createCodeSection() = emitCode() // Order matters! We need to fill the identifiers before we can extract the locals.
    .let { Create.section(Section.Code, listOf((emitLocals() + it + Opcode.end).encode()).encode()) }

private fun emitLocals(): ByteArray = identifiers
    .run { if (isEmpty()) listOf() else listOf(unsignedLeb128(size) + ValueType.f32) }
    .encode()

private fun AST.emitCode() = map { it.emit() }.reduce { acc, cur -> acc + cur }

private object Create {

    val functions: List<String> = listOf("main")

    fun section(section: Section, data: ByteArray) = section + data.encode()

    fun printFunctionImport() = "env".encode() +
            "print".encode() +
            ExportType.Function +
            0x00

    fun printFunctionType() = FunctionType +
            ValueType.f32.encode() +
            EmptyArray

    fun functionType() = FunctionType +
            byteArrayOf().encode() +
            EmptyArray

    fun runExportType() = "run".encode() +
            ExportType.Function +
            functions.size.toByte()
}

fun Node.emit(): ByteArray = also { log("Emitting Program Node $it") }
    .run {
        when (this) {
            is Print -> emitPrint()
            is VariableAndAssignmentDeclaration -> emitVariableAndAssignmentDeclaration()
            else -> throw EmitterException("Unknown program node: $this")
        }
    }

private fun Print.emitPrint(): ByteArray = also { log("Emitting Print Statement") }
    .let { expression.emit() + Opcode.call + unsignedLeb128(0) }

private fun VariableAndAssignmentDeclaration.emitVariableAndAssignmentDeclaration(): ByteArray = also { log("Emitting Variable And Assignment Declaration") }
    .let { expression.emit() + identifier.set().emit() }

private fun ExpressionNode.emit(): ByteArray = also { log("Emitting Expression $it") }
    .let {
        when (this) {
            is NumberNode -> Opcode.f32_const + number.toIEEE754Array()
            is IdentifierNode -> when (set) {
                true -> Opcode.set_local + getIdentifier(value)
                false -> Opcode.get_local + getIdentifier(value)
            }
            else -> throw EmitterException("Unknown expression: $this")
        }
    }

private val identifiers: MutableList<String> = mutableListOf()

private fun getIdentifier(identifierNodeValue: String): ByteArray = with(identifiers) {
    if (!contains(identifierNodeValue)) add(identifierNodeValue)
    indexOf(identifierNodeValue)
}.let { byteArrayOf(unsignedLeb128(it)) }
