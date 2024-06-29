package com.someguy.storage.coordinates

import com.someguy.storage.classstored.ClassStored
import com.someguy.storage.position.StorePosition
import com.someguy.storage.store.Store

// From the Cobblemon Mod this is tweaked from
/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * package com.cobblemon.mod.common.api.storage
 * data class StoreCoordinates<P: StorePosition>
 */

// Eight's implementation
data class StoreCoordinates<P: StorePosition,C: ClassStored> (
    val store: Store<P, C>, // Dangerous wildcard ??
    val position: P
) {
    fun get() = store[position]
    fun remove() = store.remove(position)
}
