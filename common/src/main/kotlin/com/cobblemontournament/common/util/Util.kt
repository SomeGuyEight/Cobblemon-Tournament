package com.cobblemontournament.common.util

import java.util.UUID

// TODO better file name that 'Util'...

inline infix fun Boolean?.ifTrue(action: () -> Unit) {
    if (this == true) action.invoke()
}

fun UUID?.shortUUID(length: Int = 8): String {
    return this?.toString()?.substring(0, length) ?: "null-uuid"
}

fun ceilToPowerOfTwo(value: Int): Int {
    val maxBitInt = Integer.highestOneBit(value)
    return if (value != 0 && (value xor maxBitInt) == 0) {
        value
    } else {
        maxBitInt shl 1
    }
}

fun <T, C: MutableCollection<T>> C.addIf(element: T, predicate: (T) -> Boolean): Boolean {
    return if (predicate(element)) this.add(element) else false
}
