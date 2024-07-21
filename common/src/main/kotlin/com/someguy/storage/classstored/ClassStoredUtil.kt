package com.someguy.storage.classstored

import java.util.UUID

object ClassStoredUtil
{
    fun <C: ClassStored,M: Map<UUID,C>> M.shallowCopy(): MutableMap <UUID,C> {
        val copy = mutableMapOf <UUID,C>()
        this.forEach { copy[it.key] = it.value }
        return copy
    }
}