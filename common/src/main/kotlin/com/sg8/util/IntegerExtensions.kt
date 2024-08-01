package com.sg8.util

fun Int.ceilToPowerOfTwo(): Int {
    val maxBitInt = Integer.highestOneBit(this)
    return if (this != 0 && (this xor maxBitInt) == 0) {
        this
    } else {
        maxBitInt shl 1
    }
}
