package sample

import sample.emit.*

fun AST.emit(): ByteArray = createHeader() +
        createModuleVersion() +
        createTypeSection() +
        createImportSection() +
        createFunctionSection() +
        createExportSection() +
        createCodeSection()
