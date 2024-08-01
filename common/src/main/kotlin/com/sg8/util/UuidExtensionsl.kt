package com.sg8.util

import java.util.UUID

fun UUID?.short(length: Int = 8): String {
    return this?.toString()?.substring(0, length) ?: "null-uuid"
}
