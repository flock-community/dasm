package compiler.utils

import kotlinx.cinterop.toKString
import platform.posix.getenv

fun log(string: String) = if (enableLogging) println(string) else Unit

private val enableLogging = getenv("DASM_LOG_ENABLED")?.toKString().toBoolean()
