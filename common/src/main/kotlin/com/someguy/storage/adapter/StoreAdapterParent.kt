package com.someguy.storage.adapter

/* From the Cobblemon Mod I adapted this script from
 *
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/
 *
 *
 * @author NickImpact
 * @since August 22nd, 2022
 *
 * package com.cobblemon.mod.common.api.storage.adapter
 * abstract class CobblemonAdapterParent<S> : CobblemonAdapter<S>
 *
 */

import com.someguy.storage.store.Store
import com.someguy.storage.position.StorePosition
import com.someguy.storage.classstored.ClassStored
import java.util.UUID

/** Provides a generic layer for adapters which are expected to allow for children */

// Eight's implementation
@Suppress("MemberVisibilityCanBePrivate")
abstract class StoreAdapterParent<Ser> : StoreAdapter<Ser>
{
    val children: MutableList<StoreAdapter<*>> = mutableListOf()

    fun with(vararg children: StoreAdapter<*>) : StoreAdapter<Ser>
    {
        this.children.addAll(children)
        return this
    }

    override fun <P: StorePosition,C: ClassStored,St: Store<P, C>> load(storeClass: Class<St>, uuid: UUID): St?
    {
        return this.provide(storeClass, uuid)
            ?: children.firstNotNullOfOrNull { it.load(storeClass, uuid) }
    }

    abstract fun <P: StorePosition,C: ClassStored,St: Store<P, C>> provide(storeClass: Class<St>, uuid: UUID): St?
}
