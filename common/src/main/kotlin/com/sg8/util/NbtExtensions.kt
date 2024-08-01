package com.sg8.util

import net.minecraft.nbt.CompoundTag
import java.lang.NullPointerException
import java.util.UUID

/**
 * @return true if [string] was put to this or
 * false if the value was null & nothing was put to this
 */
fun CompoundTag.putIfNotNull(key: String, string: String?): Boolean {
    return string?.let { this.putString(key, it).let { true } } ?: false
}

/**
 * @return `true` if [int] was put to this or
 * false if the value was null & nothing was put to this
 */
fun CompoundTag.putIfNotNull(key: String, int: Int?): Boolean {
    return int?.let { this.putInt(key, it).let { true } } ?: false
}

/**
 * @return true if [boolean] was put to this or
 * false if the value was null & nothing was put to this
 */
fun CompoundTag.putIfNotNull(key: String, boolean: Boolean?): Boolean {
    return boolean?.let { this.putBoolean(key, it).let { true } } ?: false
}

/**
 * @return `true` if [uuid] was put to this or
 * false if the value was null & nothing was put to this
 */
fun CompoundTag.putIfNotNull(key: String, uuid: UUID?): Boolean {
    return uuid?.let { this.putUUID(key, it).let { true } } ?: false
}

/**
 * @return `true` if [string] was put to this or
 * false if the value was null & nothing was put to this
 */
fun <E: Enum<E>> CompoundTag.putIfNotNull(key: String, string: E?): Boolean {
    return string?.let { this.putString(key, it.name).let { true } } ?: false
}

fun CompoundTag.getUuidOrDefault(key: String, default: UUID): UUID {
    return this.getUuidOrNull(key) ?: run { default }
}

fun CompoundTag.getStringOrDefault(key: String, default: String): String {
    return this.getStringOrNull(key) ?: run { default }
}

fun CompoundTag.getIntOrDefault(key: String, default: Int): Int {
    return this.getIntOrNull(key) ?: run { default }
}

fun CompoundTag.getBooleanOrDefault(key: String, default: Boolean): Boolean {
    return this.getBooleanOrNull(key) ?: run { default }
}

fun CompoundTag.getUuidOrNull(key: String): UUID? {
    return if (this.contains(key)) this.getUUID(key) else null
}

fun CompoundTag.getStringOrNull(key: String): String? {
    return if (this.contains(key)) this.getString(key) else null
}

fun CompoundTag.getIntOrNull(key: String): Int? {
    return if (this.contains(key)) this.getInt(key) else null
}

fun CompoundTag.getBooleanOrNull(key: String): Boolean? {
    return if (this.contains(key)) this.getBoolean(key) else null
}

/**
 * @return A [loose] [E] constant match or `null`
 */
inline fun <reified E : Enum<E>> CompoundTag.getConstantOrNull(key: String): E? {
    return if (this.contains(key)) this.getString(key).getConstantOrNull<E>() else null
}

/**
 * @return An exact [E] constant match or `null`
 */
inline fun <reified E : Enum<E>> CompoundTag.getConstantStrictOrNull(key: String): E? {
    return if (this.contains(key)) this.getString(key).getConstantStrictOrNull<E>() else null
}

/**
 * @return An exact [E] constant match or throws
 * @throws Exception If this [CompoundTag] does not contain [key]
 * @throws NullPointerException If the [loose] string value from this [CompoundTag] & [key]
 * does not have an [E] constant.loose() match
 */
inline fun <reified E : Enum<E>> CompoundTag.getConstant(key: String): E {
    if (this.contains(key)) {
        return this.getString(key).getConstant<E>()
    }
    throw Exception("Nbt Compound did not contain key '$key'")
}

/**
 * @return An exact [E] constant match or throws
 * @throws Exception If this [CompoundTag] does not contain [key]
 * @throws NullPointerException If string value from this [CompoundTag] & [key]
 * does not have an exact [E] constant match
 */
inline fun <reified E : Enum<E>> CompoundTag.getConstantStrict(key: String): E {
    if (this.contains(key)) {
        return this.getString(key).getConstantStrict<E>()
    }
    throw Exception("Nbt Compound did not contain key '$key'")
}
