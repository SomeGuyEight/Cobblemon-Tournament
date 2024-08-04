package com.sg8.util

import java.lang.NullPointerException


/**
 * @return (`this`) -> [filterNot] & [lowercase]
 *
 *          this.filterNot (' ', '_', '-')
 *          this.lowercase
 */
fun String.loose(): String {
    return this
        .filterNot { it == ' ' || it == '_' || it == '-' }
        .lowercase()
}

/**
 * Baeldung Author: Kai Yuan
 *
 * Original Link: https://www.baeldung.com/kotlin/convert-camel-case-snake-case
 */
fun String.snakeToCamelCase() = replace("_[a-z]".toRegex()) { it.value.last().uppercase() }

fun String.snakeToKebabCase() = this.replace(oldChar = '_', newChar = '-')

/**
 * `loose` = a value processed by [loose]
 *
 * @return A `loose` [E] constant match or `null`
 */
inline fun <reified E : Enum<E>> String.getConstantOrNull(): E? {
    val looseString = this.loose()
    return E::class.java.enumConstants.firstOrNull { it.name.loose() == looseString }
}

/**
 * @return An exact [E] enum class constant match or `null`
 */
inline fun <reified E : Enum<E>> String.getConstantStrictOrNull(): E? {
    return E::class.java.enumConstants.firstOrNull { it.name == this }
}

/**
 * `loose` = a value processed by [loose]
 *
 * @return A `loose` [E] constant match or throws [NullPointerException]
 */
inline fun <reified E : Enum<E>> String.getConstant(): E {
    val looseString = this.loose()
    return E::class.java.enumConstants
        .firstOrNull { it.name.loose() == looseString }
        ?: throw NullPointerException(
            "${E::class.java.simpleName} did not contain a loose match of '$looseString'"
        )
}

/**
 * @return An exact [E] enum class constant match or throws [NullPointerException]
 */
inline fun <reified E : Enum<E>> String.getConstantStrict(): E {
    return E::class.java.enumConstants
        .firstOrNull { it.name == this }
        ?: throw NullPointerException("${E::class.java.simpleName} did not contain '$this'")
}
