package sample

fun TransformedProgram.emit(): ByteArray {

    val magicModuleHeader = arrayOf(0x00, 0x61, 0x73, 0x6d)
    val moduleVersion = arrayOf(0x01, 0x00, 0x00, 0x00)

    val codes = this.map {
        when (it) {
            //is StatementNode.VariableAndAssignmentDeclaration -> emitVariableAndAssigmentDeclaration(it)
            else -> error("Unknown declaration type")
        }
    }

    val opcodes = arrayOf(
        magicModuleHeader,
        moduleVersion,
        createTypeSection()


    )

    return toString().encodeToByteArray()
}

fun emitVariableAndAssigmentDeclaration(it: StatementNode.VariableAndAssignmentDeclaration): Array<Int> = arrayOf(
    Opcode.f32_const.waCode
)

fun createTypeSection() = createSection(
    Section.type,
    encodeVector(arrayOf(printFunctionType(), TODO("implement and call type functions")))
)

fun printFunctionType() = arrayOf(
    functionType,
    encodeVector(arrayOf(Valtype.f32)),
    emptyArray
)

fun funcTypes(program: Program) = program.map {

    arrayOf(
        functionType,
        //encodeVector(it.),
        emptyArray
    )

}

fun createSection(section: Section, data: Array<Any>)= arrayOf(
    section.waCode,
    encodeVector(data)
)

fun encodeVector(data: Array<Any>): Array<Any> {

    TODO("Not yet implemented")
}

// http://webassembly.github.io/spec/core/binary/types.html#function-types
val functionType = 0x60;
val emptyArray = 0x0;

// https://webassembly.github.io/spec/core/binary/modules.html#sections
enum class Section(val waCode: Int) {
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
    code(10),
    `data`(11)
}

enum class Opcode(val waCode: Int) {
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
    i32_trunc_f32_s(0xa8)
}

// https://webassembly.github.io/spec/core/binary/types.html
enum class Valtype(val waCode: Int) {
    i32(0x7f),
    f32(0x7d)
}


