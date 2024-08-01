package com.sg8.storage

// From the Cobblemon Mod this is tweaked from
/*
 * Copyright (C) 2023 Cobblemon Contributors
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can get one at https://mozilla.org/MPL/2.0/.
 *
 * package com.cobblemon.mod.common.api.storage
 * data class StoreCoordinates<P: StorePosition>
 */

// Eight's implementation
data class StoreCoordinates<P : StorePosition, T : TypeStored> (
    val store: Store<P, T>,
    val position: P
) {
    fun get() = store[position]
    fun remove() = store.remove(position)
}
