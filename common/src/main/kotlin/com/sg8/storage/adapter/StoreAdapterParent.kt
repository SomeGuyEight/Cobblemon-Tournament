package com.sg8.storage.adapter

/* From the Cobblemon Mod I adapted this script from
 *
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can get one at https://mozilla.org/MPL/2.0/
 *
 *
 * @author NickImpact
 * @since August 22nd, 2022
 *
 * package com.cobblemon.mod.common.api.storage.adapter
 * abstract class CobblemonAdapterParent<S> : CobblemonAdapter<S>
 *
 */

import com.sg8.storage.Store
import com.sg8.storage.StorePosition
import com.sg8.storage.TypeStored
import java.util.UUID

/** Provides a generic layer for adapters which are expected to allow for children */

// Eight's implementation
abstract class StoreAdapterParent<Ser> : StoreAdapter<Ser> {

    private val children: MutableList<StoreAdapter<*>> = mutableListOf()

    fun with(vararg children: StoreAdapter<*>): StoreAdapter<Ser> {
        this.children.addAll(children)
        return this
    }

    override fun <P : StorePosition, T : TypeStored, S : Store<P, T>> load(
        storeClass: Class<S>,
        uuid: UUID,
    ): S? {
        return this.provide(storeClass, uuid)
            ?: children.firstNotNullOfOrNull { it.load(storeClass, uuid) }
    }

    abstract fun <P: StorePosition,T: TypeStored,S: Store<P, T>> provide(
        storeClass: Class<S>,
        uuid: UUID
    ): S?

}
