package sample

import sample.ProgramNode.PrintStatement
import sample.exceptions.EmitterException
import sample.utils.encode
import sample.utils.log
import sample.utils.toIEEE754Array
import sample.utils.unsignedLeb128

fun AST.emit(): ByteArray = createHeader() +
        createModuleVersion() +
        createTypeSection() +
        createImportSection() +
        createFunctionSection() +
        createExportSection() +
        createCodeSection()

private fun createHeader() = byteArrayOf(0x00, 0x61, 0x73, 0x6d)
private fun createModuleVersion() = byteArrayOf(0x01, 0x00, 0x00, 0x00)
private fun createTypeSection() = createSection(Section.type, encodeVector(listOf(createPrintFuncType(), createFuncType())))
private fun createImportSection() = createSection(Section.import, encodeVector(listOf(createPrintFunctionImport(), memoryImport())))
private fun createFunctionSection() = createSection(Section.func, encodeVector(byteArrayOf(1.toByte()))) // 1 because we assume (for now) that we have 1 function
private fun createExportSection() = createSection(Section.export, encodeVector(listOf(createRunExportType())))
private fun AST.createCodeSection() = createSection(Section.code, byteArrayOf(1, 9, 0) + map { it.emit() }.reduce { acc, cur -> acc + cur } + 11)

private fun createPrintFunctionImport() = "env".encode() +
        "print".encode() +
        ExportType.func.toByte() +
        0x00

private fun memoryImport() = "env".encode() +
        "memory".encode() +
        ExportType.mem.toByte() +
        /* limits https://webassembly.github.io/spec/core/binary/types.html#limits -indicates a min memory size of one page */
        0x00 +
        0x01

private fun createPrintFuncType() = byteArrayOf(functionType.toByte()) +
        encodeVector(byteArrayOf(Valtype.f32.toByte())) +
        emptyArray.toByte()


private fun createFuncType() = byteArrayOf(functionType.toByte()) +
        encodeVector(byteArrayOf()) +
        emptyArray.toByte()

private fun createRunExportType() = "run".encode() +
        ExportType.func.toByte() +
        1.toByte() // 1 because we assume (for now) that we have 1 callable function

private fun Node.emit(): ByteArray {
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

private fun createSection(section: Section, data: ByteArray) = byteArrayOf(section.toByte()) + encodeVector(data)
private fun encodeVector(data: ByteArray) = byteArrayOf(unsignedLeb128(data.size.toLong())) + data
private fun encodeVector(data: List<ByteArray>) = byteArrayOf(unsignedLeb128(data.size.toLong())) + data.reduce { acc, bytes -> acc + bytes }

const val functionType = 0x60
const val emptyArray = 0x0

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

// https://webassembly.github.io/spec/core/binary/types.html
enum class Valtype(private val code: Int) {
    i32(0x7f),
    f32(0x7d);

    fun toByte() = code.toByte()
}

// https://webassembly.github.io/spec/core/binary/modules.html#sections
enum class Section(private val intCode: Int) {
    custom(0),
    type(1),
    import(2),
    func(3),
    table(4),
    memory(5),
    global(6),
    export(7),
    start(8),
    element(9),
    code(10);
    //data(11)

    fun toByte() = intCode.toByte()
}

enum class ExportType(private val code: Int) {
    func(0x00),
    table(0x01),
    mem(0x02),
    global(0x03);

    fun toByte() = code.toByte()
}
